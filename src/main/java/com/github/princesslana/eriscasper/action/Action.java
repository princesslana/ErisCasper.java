package com.github.princesslana.eriscasper.action;

import org.immutables.value.Value;

public interface Action<D> {
  @Value.Parameter
  D getData();
}
