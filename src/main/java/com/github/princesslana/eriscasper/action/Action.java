package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.rest.Route;
import java.util.Optional;
import org.immutables.value.Value;

/** An Action is a {@link Route} with the data required to execute it. */
@Value.Immutable
public interface Action<I, O> {
  /**
   * Get the {@link Route} that this Action will call.
   *
   * @return the Route this Action will call
   */
  Route<I, O> getRoute();

  /**
   * Get the data that will be sent to the {@link Route}. This is {@link Optional} as not all {@link
   * Route}s require data.
   *
   * @return the data to be sent to the Route
   */
  Optional<I> getData();

  public static <O> Action<Void, O> of(Route<Void, O> route) {
    return ImmutableAction.<Void, O>builder().route(route).build();
  }

  public static <I, O> Action<I, O> of(Route<I, O> route, I data) {
    return ImmutableAction.<I, O>builder().route(route).data(data).build();
  }
}
