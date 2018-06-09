package com.github.princesslana.eriscasper.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.princesslana.eriscasper.data.Data;
import com.github.princesslana.eriscasper.data.util.Jackson;
import com.google.common.collect.ImmutableList;
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

  private HttpMethod method;
  private String path;
  private Function<Rq, RequestContent> requestHandler;
  private Function<Response, Rs> responseHandler;

  public Route(
      HttpMethod method,
      String path,
      Function<Rq, RequestContent> requestHandler,
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
    RequestContent content = requestHandler.apply(rq);

    RequestBody body =
        content.getBody().map(b -> RequestBody.create(MEDIA_TYPE_JSON, b)).orElse(null);

    String queryString = content.getQueryString().map(q -> "?" + q).orElse("");

    String url = String.format("%s%s%s", URL, getPath(), queryString);

    return new Request.Builder().method(method.get(), body).url(url);
  }

  public Function<Response, Rs> getResponseHandler() {
    return responseHandler;
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

  private static Function<Void, RequestContent> noContent() {
    return r -> ImmutableRequestContent.builder().build();
  }

  public static <Rq> Function<Rq, RequestContent> queryString(Function<Rq, String> f) {
    return rq -> ImmutableRequestContent.builder().queryString(f.apply(rq)).build();
  }

  private static <Rq> Function<Rq, RequestContent> jsonRequestBody() {
    return rq -> ImmutableRequestContent.builder().body(JACKSON.writeValueAsString(rq)).build();
  }

  public static <Rq> Function<ImmutableList<Rq>, RequestContent> jsonArrayRequstBody() {
    return jsonRequestBody();
  }

  public static Function<Response, Void> noResponse() {
    return rs -> null;
  }

  private static <Rs> Function<Response, Rs> jsonResponse(Class<Rs> rs) {
    return r -> Data.fromJson(r.body().string(), rs);
  }

  @SuppressWarnings("unchecked")
  public static <Rs> Function<Response, ImmutableList<Rs>> jsonArrayResponse(Class<Rs> rs) {
    return r ->
        (ImmutableList<Rs>)
            JACKSON.readValue(
                r.body().string(),
                TypeFactory.defaultInstance().constructCollectionType(ImmutableList.class, rs));
  }

  public static <Rs> Route<Void, Rs> delete(String path, Class<Rs> rsClass) {
    return new Route<Void, Rs>(HttpMethod.DELETE, path, noContent(), jsonResponse(rsClass));
  }

  public static <Rs> Route<Void, Rs> get(String path, Class<Rs> rsClass) {
    return get(path, noContent(), jsonResponse(rsClass));
  }

  public static <Rq, Rs> Route<Rq, Rs> get(
      String path, Function<Rq, RequestContent> rqHandler, Function<Response, Rs> rsHandler) {
    return new Route<Rq, Rs>(HttpMethod.GET, path, rqHandler, rsHandler);
  }

  public static <Rq, Rs> Route<Rq, Rs> patch(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return new Route<Rq, Rs>(HttpMethod.PATCH, path, jsonRequestBody(), jsonResponse(rsClass));
  }

  public static <Rq, Rs> Route<Rq, Rs> post(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return post(path, jsonRequestBody(), jsonResponse(rsClass));
  }

  public static <Rq, Rs> Route<Rq, Rs> post(
      String path, Function<Rq, RequestContent> rqHandler, Function<Response, Rs> rsHandler) {
    return new Route<Rq, Rs>(HttpMethod.POST, path, rqHandler, rsHandler);
  }

  public static <Rq, Rs> Route<Rq, Rs> put(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return new Route<Rq, Rs>(HttpMethod.PUT, path, jsonRequestBody(), jsonResponse(rsClass));
  }
}
