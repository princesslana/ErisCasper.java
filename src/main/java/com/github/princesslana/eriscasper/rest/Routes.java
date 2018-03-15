package com.github.princesslana.eriscasper.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.BotToken;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routes {

  private static final Logger LOG = LoggerFactory.getLogger(Routes.class);

  private final BotToken token;

  private final OkHttpClient client;
  private final ObjectMapper jackson;

  public Routes(BotToken token, OkHttpClient client, ObjectMapper jackson) {
    this.token = token;
    this.client = client;
    this.jackson = jackson;
  }

  public <Rs> Single<Rs> execute(Route<Void, Rs> route) {
    return execute(route, null);
  }

  public <Rq, Rs> Single<Rs> execute(Route<Rq, Rs> route, Rq data) {
    return Single.fromCallable(
            () -> {
              LOG.debug("Executing: {}...", route);

              RequestBody body =
                  data == null
                      ? null
                      : RequestBody.create(
                          MediaType.parse("application/json"), jackson.writeValueAsString(data));

              Request rq =
                  new Request.Builder()
                      .method(route.getMethod().get(), body)
                      .url(route.getUrl())
                      .header("Authorization", "Bot " + token.unwrap())
                      .build();

              try (Response rs = client.newCall(rq).execute()) {
                String rsBody = rs.body().string();

                LOG.debug("Response: {}", rs);
                LOG.debug("Headers: {}", rs.headers());
                LOG.debug("Body: {}", rsBody);

                if (!rs.isSuccessful()) {
                  throw new IllegalStateException(String.format("Request failed: %s", rs));
                }

                return jackson.readValue(rsBody, route.getResponseClass());
              }
            })
        .doOnSuccess(r -> LOG.debug("Done: {} -> {}.", route, r))
        .doOnError(e -> LOG.warn("Error: {} - {}.", route, e));
  }
}
