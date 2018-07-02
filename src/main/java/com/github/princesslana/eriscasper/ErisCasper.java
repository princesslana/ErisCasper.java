package com.github.princesslana.eriscasper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.util.Jackson;
import com.github.princesslana.eriscasper.gateway.Gateway;
import com.github.princesslana.eriscasper.gateway.Payloads;
import com.github.princesslana.eriscasper.repository.RepositoryManager;
import com.github.princesslana.eriscasper.rest.RouteCatalog;
import com.github.princesslana.eriscasper.rest.Routes;
import com.github.princesslana.eriscasper.util.OkHttp;
import com.github.princesslana.eriscasper.util.Shard;
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
  private final ObjectMapper jackson = Jackson.newObjectMapper();
  private final Payloads payloads = new Payloads(jackson);

  private final Routes routes;
  private final Optional<Shard> shard;

  private ErisCasper(BotToken token, Optional<Shard> shard) {
    this.token = token;
    this.shard = shard;
    routes = new Routes(token, httpClient, jackson);
  }

  private Observable<Event> getEvents() {
    Gateway gateway = Gateway.create(httpClient, payloads);
    return Single.just(RouteCatalog.getGateway())
        .observeOn(Schedulers.io())
        .flatMap(routes::execute)
        .toObservable()
        .flatMap(gr -> gateway.connect(gr.getUrl(), token, shard))
        .observeOn(Schedulers.computation())
        .share();
  }

  public void run(Bot bot) {
    Observable<Event> events = getEvents();

    RepositoryManager rm = RepositoryManager.create(events);

    bot.apply(new BotContext(events, routes, rm))
        .doOnError(t -> LOG.warn("Exception thrown by Bot", t))
        .blockingAwait();
  }

  public static ErisCasper create() {
    return create(
        CONFIG
            .get("ec.token")
            .orElseThrow(() -> new ErisCasperFatalException("ec.token not provided")));
  }

  public static ErisCasper create(String token) {
    return new ErisCasper(BotToken.of(token), Shard.fromConfig(CONFIG));
  }

  public static ErisCasper create(String token, int shardNumber, int shardTotal) {
    Shard shard = new Shard(shardNumber, shardTotal);
    return new ErisCasper(BotToken.of(token), Optional.of(shard));
  }
}
