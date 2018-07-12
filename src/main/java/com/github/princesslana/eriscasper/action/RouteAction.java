package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.data.util.Nullable;
import com.github.princesslana.eriscasper.rest.Route;
import io.reactivex.Completable;
import org.immutables.value.Value;

@Value.Immutable
public abstract class RouteAction<I, O> implements Action {

  public abstract Route<I, O> getRoute();

  public abstract Nullable<I> getData();

  @Override
  public Completable apply(ActionContext context) {
    return context.getRoutes().execute(getRoute(), getData().orNull()).toCompletable();
  }

  static <O> RouteAction<Void, O> of(Route<Void, O> route) {
    return of(route, Nullable.ofNull());
  }

  static <I, O> RouteAction<I, O> of(Route<I, O> route, I data) {
    return of(route, Nullable.ofNullable(data));
  }

  static <I, O> RouteAction<I, O> of(Route<I, O> route, Nullable<I> data) {
    return ImmutableRouteAction.<I, O>builder().route(route).data(data).build();
  }
}
