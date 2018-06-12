package com.github.princesslana.eriscasper.repository;

import com.google.common.collect.ImmutableMap;
import io.reactivex.functions.Function;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class FunctionData<X, Y, Z> implements Function<Z, Function<Map<X, Y>, ImmutableMap<X, Y>>> {

  private final BiFunction<Map<X, Y>, Z, ImmutableMap<X, Y>> mapBiFunction;

  public FunctionData(BiFunction<Map<X, Y>, Z, ImmutableMap<X, Y>> mapBiFunction) {
    this.mapBiFunction = mapBiFunction;
  }

  @Override
  public Function<Map<X, Y>, ImmutableMap<X, Y>> apply(Z value) {
    // pushes it into a new HashMap to reduce code redundancy just from the general pattern.
    return (map) -> mapBiFunction.apply(new HashMap<>(map), value);
  }
}
