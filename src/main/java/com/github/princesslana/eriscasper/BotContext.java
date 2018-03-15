package com.github.princesslana.eriscasper;

import com.github.princesslana.eriscasper.event.Event;
import com.github.princesslana.eriscasper.rest.Route;
import com.github.princesslana.eriscasper.rest.Routes;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class BotContext {

  private final Flowable<Event<?>> events;

  private Routes routes;

  public BotContext(Flowable<Event<?>> events, Routes routes) {
    this.events = events;
    this.routes = routes;
  }

  public Flowable<Event<?>> getEvents() {
    return events;
  }

  public <Rs> Single<Rs> execute(Route<Void, Rs> route) {
    return routes.execute(route);
  }

  public <Rq, Rs> Single<Rs> execute(Route<Rq, Rs> route, Rq request) {
    return routes.execute(route, request);
  }
}
