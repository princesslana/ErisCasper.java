package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.rest.Route;
import io.reactivex.Completable;
import org.immutables.value.Value;

@Value.Immutable
public interface VoidRouteAction extends Action {

  Route<Void, ?> getRoute();

  @Override
  default Completable execute(ActionContext context) {
    return context.getRoutes().execute(getRoute()).toCompletable();
  }

  static VoidRouteAction of(Route<Void, ?> route) {
    return ImmutableVoidRouteAction.builder().route(route).build();
  }
}
