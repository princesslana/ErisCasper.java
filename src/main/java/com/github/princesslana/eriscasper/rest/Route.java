package com.github.princesslana.eriscasper.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.data.Data;
import com.github.princesslana.eriscasper.data.util.Jackson;
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

  private static enum Content {
    BODY,
    QUERY_STRING
  }

  protected static enum HttpMethod {
    DELETE("DELETE", Content.QUERY_STRING),
    GET("GET", Content.QUERY_STRING),
    POST("POST", Content.BODY),
    PUT("PUT", Content.BODY);

    private final String method;

    private final Content content;

    private HttpMethod(String method, Content content) {
      this.method = method;
      this.content = content;
    }

    public String get() {
      return method;
    }

    public boolean isContent(Content rhs) {
      return content == rhs;
    }
  }

  private HttpMethod method;
  private String path;
  private Function<Rq, String> requestHandler;
  private Function<Response, Rs> responseHandler;

  public Route(
      HttpMethod method,
      String path,
      Function<Rq, String> requestHandler,
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
    RequestBody body =
        method.isContent(Content.BODY)
            ? RequestBody.create(MEDIA_TYPE_JSON, requestHandler.apply(rq))
            : null;

    String queryString =
        method.isContent(Content.QUERY_STRING) ? "?" + requestHandler.apply(rq) : "";

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

  private static Function<Void, String> noContent() {
    return r -> "";
  }

  private static <Rq> Function<Rq, String> jsonRequestBody() {
    return rq -> JACKSON.writeValueAsString(rq);
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

  public static <Rq, Rs> Route<Rq, Rs> get(
      String path, Function<Rq, String> rqHandler, Function<Response, Rs> rsHandler) {
    return new Route<Rq, Rs>(HttpMethod.GET, path, rqHandler, rsHandler);
  }

  public static <Rq, Rs> Route<Rq, Rs> post(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return new Route<Rq, Rs>(HttpMethod.POST, path, jsonRequestBody(), jsonResponse(rsClass));
  }

  public static <Rq, Rs> Route<Rq, Rs> put(String path, Class<Rq> rqClass, Class<Rs> rsClass) {
    return new Route<Rq, Rs>(HttpMethod.PUT, path, jsonRequestBody(), jsonResponse(rsClass));
  }
}
