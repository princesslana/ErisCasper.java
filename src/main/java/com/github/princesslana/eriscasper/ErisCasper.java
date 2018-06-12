package com.github.princesslana.eriscasper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.util.Jackson;
import com.github.princesslana.eriscasper.gateway.Gateway;
import com.github.princesslana.eriscasper.gateway.Payloads;
import com.github.princesslana.eriscasper.gateway.commands.RequestGuildMembers;
import com.github.princesslana.eriscasper.gateway.commands.UpdatePresence;
import com.github.princesslana.eriscasper.gateway.commands.UpdateVoiceState;
import com.github.princesslana.eriscasper.repository.RepositoryManager;
import com.github.princesslana.eriscasper.rest.RouteCatalog;
import com.github.princesslana.eriscasper.rest.Routes;
import com.github.princesslana.eriscasper.util.OkHttp;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.Nullable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErisCasper {

  private static final Logger LOG = LoggerFactory.getLogger(ErisCasper.class);

  private final BotToken token;

  private final OkHttpClient httpClient = OkHttp.newHttpClient();
  private final ObjectMapper jackson = Jackson.newObjectMapper();
  private final Payloads payloads = new Payloads(jackson);

  @Nullable private final Integer[] shard;

  private final Routes routes;

  private Gateway gateway;

  private ErisCasper(BotToken token, Integer[] shard) {
    this.token = token;
    this.shard = shard;
    routes = new Routes(token, httpClient, jackson);
  }

  private Observable<Event> getEvents() {
    gateway = Gateway.create(httpClient, payloads);
    return Single.just(RouteCatalog.getGateway())
        .observeOn(Schedulers.io())
        .flatMap(routes::execute)
        .toObservable()
        .flatMap(gr -> gateway.connect(gr.getUrl(), token, shard))
        .observeOn(Schedulers.computation())
        .share();
  }

  public Integer getShardNum() {
    return shard == null ? -1 : shard[0];
  }

  public void run(Bot bot) {
    Observable<Event> events = getEvents();

    RepositoryManager rm = RepositoryManager.create(events);
    bot.apply(new BotContext(this, events, routes, rm))
        .doOnError(t -> LOG.warn("Exception thrown by Bot", t))
        .blockingAwait();
  }

  public void shutdownGracefully() {
    gateway.shutdownGracefully();
  }

  public Completable requestGuildMembers(RequestGuildMembers request) {
    return gateway.requestGuildMembers(request);
  }

  public Completable updatePresence(UpdatePresence update) {
    return gateway.updatePresence(update);
  }

  public Completable updateVoiceState(UpdateVoiceState update) {
    return gateway.updateVoiceState(update);
  }

  public static ErisCasper create() {
    String token;
    if (System.getenv().containsKey("EC_TOKEN")) {
      token = System.getenv("EC_TOKEN");
    } else {
      token = System.getProperty("ec.token", null);
    }
    if (token == null) {
      throw new ErisCasperFatalException("Token cannot be null upon creation.");
    }
    String shardNumRaw = System.getProperty("shard.num", null);
    String shardTotalRaw = System.getProperty("shard.total", null);
    if (shardNumRaw != null
        && shardNumRaw.matches("\\d+")
        && shardTotalRaw != null
        && shardTotalRaw.matches("\\d+")) {
      int shardNum = Integer.parseInt(shardNumRaw);
      int shardTotal = Integer.parseInt(shardTotalRaw);
      return create(token, shardNum, shardTotal);
    }
    return create(token);
  }

  public static ErisCasper create(String token) {
    if (token == null) {
      throw new ErisCasperFatalException("Token cannot be null upon creation.");
    }
    return new ErisCasper(BotToken.of(token), null);
  }

  public static ErisCasper create(int shardNum, int shardTotal) {
    if (System.getenv().containsKey("EC_TOKEN")) {
      return create(System.getenv("EC_TOKEN"), shardNum, shardTotal);
    }
    return create(System.getProperty("ec.token"), shardNum, shardTotal);
  }

  public static ErisCasper create(String token, int shardNum, int shardTotal) {
    assertShard(shardNum, shardTotal);
    if (token == null) {
      throw new ErisCasperFatalException("Token cannot be null upon creation.");
    }
    return new ErisCasper(BotToken.of(token), new Integer[] {shardNum, shardTotal});
  }

  private static void assertShard(int shardNum, int shardTotal) {
    if (shardNum > shardTotal || shardTotal < 0 || shardNum < 0) {
      throw new ErisCasperFatalException(
          "Could not apply sharding with [" + shardNum + ", " + shardTotal + "]");
    }
  }
}
