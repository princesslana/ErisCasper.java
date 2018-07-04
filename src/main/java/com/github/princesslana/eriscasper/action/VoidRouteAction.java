package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.rest.Route;
import io.reactivex.Completable;
import org.immutables.value.Value;

@Value.Immutable
public interface VoidRouteAction<O> extends Action {

  Route<Void, O> getRoute();

  @Override
  default Completable execute(ActionContext context) {
    return context
        .getRoutes()
        .map(routes -> routes.execute(getRoute()).toCompletable())
        .orElse(Completable.complete());
  }

  static <O> VoidRouteAction<O> of(Route<Void, O> route) {
    return ImmutableVoidRouteAction.<O>builder().route(route).build();
  }
}
