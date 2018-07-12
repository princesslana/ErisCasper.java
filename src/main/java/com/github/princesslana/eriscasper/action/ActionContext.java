package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.gateway.Gateway;
import com.github.princesslana.eriscasper.rest.Routes;
import org.immutables.value.Value;

@Value.Immutable
public interface ActionContext {

  Routes getRoutes();

  Gateway getGateway();
}
