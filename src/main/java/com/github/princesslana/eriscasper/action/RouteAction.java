package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.rest.Route;
import io.reactivex.Completable;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public abstract class RouteAction<I, O> implements Action {

  public abstract Route<I, O> getRoute();

  public abstract Optional<I> getData();

  @Override
  public Completable apply(ActionContext context) {
    return context.getRoutes().execute(getRoute(), getData().orElse(null)).toCompletable();
  }

  static <O> RouteAction<Void, O> of(Route<Void, O> route) {
    return ImmutableRouteAction.<Void, O>builder().route(route).build();
  }

  static <I, O> RouteAction<I, O> of(Route<I, O> route, I data) {
    return ImmutableRouteAction.<I, O>builder().route(route).data(data).build();
  }

  static <I, O> RouteAction<I, O> of(Route<I, O> route, Optional<I> data) {
    return ImmutableRouteAction.<I, O>builder().route(route).data(data).build();
  }
}
