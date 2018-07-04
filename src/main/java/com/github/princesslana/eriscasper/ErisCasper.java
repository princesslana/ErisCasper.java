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
import com.google.common.base.Preconditions;
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
  private final Optional<ShardPayload> shard;

  private ErisCasper(BotToken token, Optional<ShardPayload> shard) {
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

    return new ErisCasper(BotToken.of(token), shardFromConfig(CONFIG));
  }

  public static ErisCasper create(String token, int shardNumber, int shardTotal) {
    assertShard(shardNumber, shardTotal);
    return new ErisCasper(
        BotToken.of(token), Optional.of(ShardPayload.of(shardNumber, shardTotal)));
  }

  private static void assertShard(long shard, long total) {
    Preconditions.checkArgument(shard >= 0, "Shard number must be greater than or equal to 0.");
    Preconditions.checkArgument(total >= 1, "Shard total must be greater than or equal to 1.");
    Preconditions.checkState(shard < total, "Shard number must be less than the shard total.");
  }

  public static Optional<ShardPayload> shardFromConfig(Properlty config) {
    Optional<Integer> shard = config.getInt("ec.shard.id");
    Optional<Integer> total = config.getInt("ec.shard.total");
    if (shard.isPresent() || total.isPresent()) {
      ErisCasperFatalException exception =
          new ErisCasperFatalException(
              "Failed to resolve both sharding values when only one was provided.");
      return Optional.of(
          shard
              .map(
                  s ->
                      total
                          .map(
                              t -> {
                                assertShard(s, t);
                                return ShardPayload.of(s, t);
                              })
                          .orElseThrow(() -> exception))
              .orElseThrow(() -> exception));
    }
    return Optional.empty();
  }
}
