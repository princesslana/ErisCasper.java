package com.github.princesslana.eriscasper.event;

import org.immutables.value.Value;

public interface Event<D> {
  @Value.Parameter
  D getData();
}
