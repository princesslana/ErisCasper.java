package com.github.princesslana.eriscasper.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.princesslana.eriscasper.data.Data;
import com.github.princesslana.eriscasper.data.util.Jackson;
import com.github.princesslana.eriscasper.immutable.Tuple;
import com.google.common.collect.ImmutableList;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.immutables.value.Value;

public class Route<Rq, Rs> {

  private static final String VERSION = "v6";
  private static final String URL = String.format("https://discordapp.com/api/%s", VERSION);

  private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

  private static final ObjectMapper JACKSON = Jackson.newObjectMapper();

  public static enum Content {
    BODY,
    QUERY_STRING,
    BOTH
  }

  protected static enum HttpMethod {
    DELETE("DELETE"),
    GET("GET"),
    PATCH("PATCH"),
    POST("POST"),
    PUT("PUT");

    private final String method;

    HttpMethod(String method) {
      this.method = method;
    }

    public String get() {
      return method;
    }
  }

  @Tuple
  @Value.Immutable
  public static interface Form {
    @Nullable
    String getQuery();

    @Nullable
    String getForm();
  }

  private HttpMethod method;
  private Content content;
  private String path;
  private Function<Rq, Form> requestHandler;
  private Function<Response, Rs> responseHandler;

  public Route(
      HttpMethod method,
      Content content,
      String path,
      Function<Rq, Form> requestHandler,
      Function<Response, Rs> responseHandler) {
    this.method = method;
    this.content = content;
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
    Form tuple = requestHandler.apply(rq);
    RequestBody body =
        content != Content.QUERY_STRING
            ? RequestBody.create(MEDIA_TYPE_JSON, tuple.getForm())
            : null;

    String queryString = content != Content.BODY ? "?" + tuple.getQuery() : "";

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

  private static Function<Void, Form> noContent() {
    return r -> FormTuple.of("", "");
  }

  public static <Rq> Function<Rq, Form> jsonRequestBody() {
    return rq -> FormTuple.of("", JACKSON.writeValueAsString(rq));
  }

  public static <Rq> Function<ImmutableList<Rq>, Form> jsonArrayRequstBody() {
    return jsonRequestBody();
  }

  public static Function<Response, Void> noResponse() {
    return rs -> null;
  }

  public static <Rs> Function<Response, Rs> jsonResponse(Class<Rs> rs) {
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
    return new Route<>(
        HttpMethod.DELETE, Content.QUERY_STRING, path, noContent(), jsonResponse(rsClass));
  }

  public static <Rs> Route<Void, Rs> get(String path, Class<Rs> rsClass) {
    return get(path, noContent(), jsonResponse(rsClass));
  }

  public static <Rq, Rs> Route<Rq, Rs> get(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return get(path, jsonRequestBody(), jsonResponse(rsClass));
  }

  public static <Rs> Route<Void, Rs> get(String path, Function<Response, Rs> rsHandler) {
    return get(path, noContent(), rsHandler);
  }

  public static <Rq, Rs> Route<Rq, Rs> get(
      String path, Function<Rq, Form> rqHandler, Function<Response, Rs> rsHandler) {
    return new Route<>(HttpMethod.GET, Content.QUERY_STRING, path, rqHandler, rsHandler);
  }

  public static <Rq, Rs> Route<Rq, Rs> patch(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return patch(path, jsonRequestBody(), jsonResponse(rsClass));
  }

  public static <Rq, Rs> Route<Rq, Rs> patch(
      String path, Function<Rq, Form> rqHandler, Function<Response, Rs> rsHandler) {
    return new Route<>(HttpMethod.PATCH, Content.BODY, path, rqHandler, rsHandler);
  }

  public static <Rq, Rs> Route<Rq, Rs> post(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return post(path, jsonRequestBody(), jsonResponse(rsClass));
  }

  public static <Rq, Rs> Route<Rq, Rs> post(
      String path, Function<Rq, Form> rqHandler, Function<Response, Rs> rsHandler) {
    return post(path, rqHandler, rsHandler, Content.BODY);
  }

  public static <Rq, Rs> Route<Rq, Rs> post(
      String path,
      Function<Rq, Form> rqHandler,
      Function<Response, Rs> rsHandler,
      Content content) {
    return new Route<>(HttpMethod.POST, content, path, rqHandler, rsHandler);
  }

  public static <Rq, Rs> Route<Rq, Rs> put(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return put(path, jsonRequestBody(), jsonResponse(rsClass), Content.BODY);
  }

  public static <Rq, Rs> Route<Rq, Rs> put(
      String path,
      Function<Rq, Form> rqHandler,
      Function<Response, Rs> rsHandler,
      Content content) {
    return new Route<>(HttpMethod.PUT, content, path, rqHandler, rsHandler);
  }
}
