package com.github.princesslana.eriscasper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.event.Event;
import com.github.princesslana.eriscasper.gateway.Gateway;
import com.github.princesslana.eriscasper.gateway.Payloads;
import com.github.princesslana.eriscasper.rest.RouteCatalog;
import com.github.princesslana.eriscasper.rest.Routes;
import com.github.princesslana.eriscasper.util.Jackson;
import com.github.princesslana.eriscasper.util.OkHttp;
import io.reactivex.Flowable;
import java.io.IOException;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErisCasper {

  private static final Logger LOG = LoggerFactory.getLogger(ErisCasper.class);

  private final BotToken token;

  private final OkHttpClient httpClient = OkHttp.newHttpClient();
  private final ObjectMapper jackson = Jackson.newObjectMapper();
  private final Payloads payloads = new Payloads(jackson);
  private final Routes routes;

  private ErisCasper(BotToken token) {
    this.token = token;

    routes = new Routes(token, httpClient, jackson);
  }

  private Flowable<Event<?>> getEvents() {
    try (Gateway gateway = new Gateway(httpClient, payloads)) {
      return routes
          .execute(RouteCatalog.getGateway())
          .toFlowable()
          .flatMap(gr -> gateway.connect(gr.getUrl(), token))
          .onBackpressureBuffer()
          .share();
    } catch (IOException e) {
      return Flowable.error(e);
    }
  }

  public void run(Bot bot) {
    bot.apply(new BotContext(getEvents(), routes)).subscribe();
  }

  public static ErisCasper create() {
    return create(System.getenv("EC_TOKEN"));
  }

  public static ErisCasper create(String token) {
    return new ErisCasper(BotToken.of(token));
  }
}
