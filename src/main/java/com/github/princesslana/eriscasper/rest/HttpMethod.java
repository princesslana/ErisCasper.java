package com.github.princesslana.eriscasper.rest;

public enum HttpMethod {
  DELETE("DELETE"),
  GET("GET"),
  PATCH("PATCH"),
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
