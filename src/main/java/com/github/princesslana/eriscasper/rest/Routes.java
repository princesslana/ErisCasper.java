package com.github.princesslana.eriscasper.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.rx.Maybes;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import java.util.Optional;
import java.util.concurrent.Callable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routes {

  private static final Logger LOG = LoggerFactory.getLogger(Routes.class);

  private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

  private final BotToken token;

  private final OkHttpClient client;
  private final ObjectMapper jackson;

  public Routes(BotToken token, OkHttpClient client, ObjectMapper jackson) {
    this.token = token;
    this.client = client;
    this.jackson = jackson;
  }

  public <O> Single<O> execute(Route<Void, O> route) {
    return execute(route, null);
  }

  public <I, O> Single<O> execute(Route<I, O> route, I data) {
    Function<Optional<RequestBody>, Request> buildRequest =
        body ->
            new Request.Builder()
                .method(route.getMethod().get(), body.orElse(null))
                .url(route.getUrl())
                .header("Authorization", "Bot " + token.unwrap())
                .build();

    return Maybes.fromNullable(data)
        .map(d -> RequestBody.create(MEDIA_TYPE_JSON, jackson.writeValueAsString(data)))
        .map(Optional::of)
        .toSingle(Optional.empty())
        .map(buildRequest)
        .flatMap(rq -> executeRequest(route, rq));
  }

  private <O> Single<O> executeRequest(Route<?, O> route, Request rq) {
    Callable<Response> execute =
        () -> {
          LOG.debug("Executing: {}...", route);
          return client.newCall(rq).execute();
        };

    Consumer<Response> close =
        r -> {
          r.close();
          LOG.debug("Closed: {}.", r);
        };

    // Single#using does not work here, as it performs the close operation immediately upon emitting
    // the response, because once it's received the response it knows it can receive no more.
    //
    // Observable#using closes the response at a more appropriate time. My feeling is that this is
    // wrong - we're tricking rxjava into thinking there is something coming next so as to delay the
    // close.
    return Observable.using(execute, Observable::just, close)
        .doOnNext(r -> LOG.debug("Done: {} -> {}.", route, r))
        .flatMapSingle(
            r ->
                r.isSuccessful()
                    ? Single.just(r)
                    : Single.error(new IllegalStateException("Unexpected response")))
        .doOnError(e -> LOG.warn("Error: {} - {}.", route, e))
        .map(rs -> jackson.readValue(rs.body().byteStream(), route.getResponseClass()))
        .firstOrError();
  }
}
