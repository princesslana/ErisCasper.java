package com.github.princesslana.eriscasper.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

@Value.Immutable
public interface Pruned {
  @JsonProperty("pruned")
  Integer getPruned();
}
