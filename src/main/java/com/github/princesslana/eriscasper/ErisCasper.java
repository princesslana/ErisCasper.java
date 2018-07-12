package com.github.princesslana.eriscasper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.gateway.ShardPayload;
import com.github.princesslana.eriscasper.data.util.Jackson;
import com.github.princesslana.eriscasper.gateway.Gateway;
import com.github.princesslana.eriscasper.gateway.Payloads;
import com.github.princesslana.eriscasper.repository.RepositoryManager;
import com.github.princesslana.eriscasper.rest.RouteCatalog;
import com.github.princesslana.eriscasper.rest.Routes;
import com.github.princesslana.eriscasper.util.OkHttp;
import com.ufoscout.properlty.Properlty;
import com.ufoscout.properlty.reader.EnvironmentVariablesReader;
import com.ufoscout.properlty.reader.SystemPropertiesReader;
import com.ufoscout.properlty.reader.decorator.ToLowerCaseAndDotKeyReader;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.Optional;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErisCasper {

  private static final Logger LOG = LoggerFactory.getLogger(ErisCasper.class);

  // last wins
  private static final Properlty CONFIG =
      Properlty.builder()
          .add(new SystemPropertiesReader())
          .add(new ToLowerCaseAndDotKeyReader(new EnvironmentVariablesReader()))
          .build();

  private final BotToken token;

  private final OkHttpClient httpClient = OkHttp.newHttpClient();

  private final Routes routes;
  private final Optional<ShardPayload> shard;

  private final Gateway gateway;

  private ErisCasper(BotToken token, Optional<ShardPayload> shard) {
    this.token = token;
    this.shard = shard;
    ObjectMapper jackson = Jackson.newObjectMapper();
    this.routes = new Routes(token, httpClient, jackson);
    this.gateway = Gateway.create(httpClient, new Payloads(jackson));
  }

  private Observable<Event> getEvents() {
    return Single.just(RouteCatalog.getGateway())
        .observeOn(Schedulers.io())
        .flatMap(routes::execute)
        .toObservable()
        .flatMap(gr -> gateway.connect(gr.getUrl(), token, shard))
        .observeOn(Schedulers.computation())
        .share();
  }

  public void run(Bot bot) {
    try {
      getEvents()
          .compose(
              evts ->
                  bot.apply(new BotContext(evts, routes, gateway, RepositoryManager.create(evts)))
                      .toObservable())
          .doOnError(t -> LOG.warn("Exception thrown by Bot", t))
          .blockingSubscribe();
    } finally {
      Schedulers.shutdown();
      httpClient.dispatcher().executorService().shutdown();
      httpClient.connectionPool().evictAll();
    }

    LOG.trace("ErisCasper#run(Bot) done.");
  }

  public static ErisCasper create() {
    return create(
        CONFIG
            .get("ec.token")
            .orElseThrow(() -> new ErisCasperFatalException("ec.token not provided")));
  }

  public static ErisCasper create(String token) {

    return new ErisCasper(BotToken.of(token), Shards.fromConfig(CONFIG));
  }

  public static ErisCasper create(String token, int shardNumber, int shardTotal) {
    ShardPayload shard = ShardPayload.of(shardNumber, shardTotal);
    Shards.check(shard);
    return new ErisCasper(BotToken.of(token), Optional.of(shard));
  }
}
