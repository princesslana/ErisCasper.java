package com.github.princesslana.eriscasper.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.data.Data;
import com.github.princesslana.eriscasper.data.util.Jackson;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Route<Rq, Rs> {

  private static final String VERSION = "v6";
  private static final String URL = String.format("https://discordapp.com/api/%s", VERSION);

  private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

  private static final ObjectMapper JACKSON = Jackson.newObjectMapper();

  protected static enum HttpMethod {
    DELETE("DELETE"),
    GET("GET"),
    POST("POST"),
    PUT("PUT");

    private final String method;

    private HttpMethod(String method) {
      this.method = method;
    }

    public String get() {
      return method;
    }
  }

  private HttpMethod method;
  private String path;
  private BiFunction<Rq, HttpMethod, Request.Builder> requestHandler;
  private Function<Response, Rs> responseHandler;

  public Route(
      HttpMethod method,
      String path,
      BiFunction<Rq, HttpMethod, Request.Builder> requestHandler,
      Function<Response, Rs> responseHandler) {
    this.method = method;
    this.path = path;
    this.requestHandler = requestHandler;
    this.responseHandler = responseHandler;
  }

  protected HttpMethod getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public Request.Builder newRequestBuilder(Rq rq) throws Exception {
    return requestHandler.apply(rq, method).url(getUrl());
  }

  public Function<Response, Rs> getResponseHandler() {
    return responseHandler;
  }

  public String getUrl() {
    return String.format("%s%s", URL, getPath());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Route)) {
      return false;
    }

    Route<?, ?> rhs = (Route<?, ?>) obj;

    return Objects.equals(path, rhs.path) && Objects.equals(method, rhs.method);
  }

  public String toString() {
    return String.format(
        "Route{path=%s, method=%s}", Objects.toString(path), Objects.toString(method));
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, method);
  }

  private static BiFunction<Void, HttpMethod, Request.Builder> noContent() {
    return (rq, m) -> new Request.Builder().method(m.get(), null);
  }

  private static <Rq> BiFunction<Rq, HttpMethod, Request.Builder> jsonRequestBody() {
    return (rq, m) ->
        new Request.Builder()
            .method(m.get(), RequestBody.create(MEDIA_TYPE_JSON, JACKSON.writeValueAsString(rq)));
  }

  private static <Rs> Function<Response, Rs> jsonResponse(Class<Rs> rs) {
    return r -> Data.fromJson(r.body().string(), rs);
  }

  public static <Rs> Route<Void, Rs> delete(String path, Class<Rs> rsClass) {
    return new Route<Void, Rs>(HttpMethod.DELETE, path, noContent(), jsonResponse(rsClass));
  }

  public static <Rs> Route<Void, Rs> get(String path, Class<Rs> rsClass) {
    return new Route<Void, Rs>(HttpMethod.GET, path, noContent(), jsonResponse(rsClass));
  }

  public static <Rs> Route<Void, Rs> get(String path, Function<Response, Rs> rsHandler) {
    return new Route<Void, Rs>(HttpMethod.GET, path, noContent(), rsHandler);
  }

  public static <Rq, Rs> Route<Rq, Rs> post(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return new Route<Rq, Rs>(HttpMethod.POST, path, jsonRequestBody(), jsonResponse(rsClass));
  }

  public static <Rq, Rs> Route<Rq, Rs> put(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return new Route<Rq, Rs>(HttpMethod.PUT, path, jsonRequestBody(), jsonResponse(rsClass));
  }
}
