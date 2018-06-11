package com.github.princesslana.eriscasper.util;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.stream.Collectors;

public class QueryStringBuilder {

  private final ImmutableMap<String, String> parameters;

  public QueryStringBuilder() {
    this(ImmutableMap.of());
  }

  private QueryStringBuilder(ImmutableMap<String, String> parameters) {
    this.parameters = parameters;
  }

  public QueryStringBuilder add(String name, String value) {
    return new QueryStringBuilder(
        ImmutableMap.<String, String>builder().putAll(parameters).put(name, value).build());
  }

  public QueryStringBuilder addSnowflake(String name, Optional<Snowflake> snowflake) {
    return snowflake.map(s -> add(name, s.unwrap())).orElse(this);
  }

  public QueryStringBuilder addBoolean(String name, Optional<Boolean> value) {
    return value.map(v -> add(name, v.toString())).orElse(this);
  }

  public QueryStringBuilder addLong(String name, Optional<Long> value) {
    return value.map(v -> addLong(name, v)).orElse(this);
  }

  public QueryStringBuilder addLong(String name, Long value) {
    return add(name, value.toString());
  }

  public String build() {
    return parameters
        .entrySet()
        .stream()
        .map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining("&"));
  }
}
