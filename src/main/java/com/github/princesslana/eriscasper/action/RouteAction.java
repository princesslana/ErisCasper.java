package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.rest.Route;
import io.reactivex.Completable;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface RouteAction<I, O> extends Action {

  Route<I, O> getRoute();

  Optional<I> getData();

  default Completable execute(ActionContext context) {
    return context
        .getRoutes()
        .map(routes -> routes.execute(getRoute(), getData().orElse(null)).toCompletable())
        .orElse(Completable.complete());
  }

  static <I, O> RouteAction<I, O> of(Route<I, O> route, I data) {
    return ImmutableRouteAction.<I, O>builder().route(route).data(data).build();
  }

  static <I, O> RouteAction<I, O> of(Route<I, O> route, Optional<I> data) {
    return ImmutableRouteAction.<I, O>builder().route(route).data(data).build();
  }
}
