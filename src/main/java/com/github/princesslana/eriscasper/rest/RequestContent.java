package com.github.princesslana.eriscasper.rest;

import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface RequestContent {
  Optional<String> getQueryString();

  Optional<String> getBody();
}
