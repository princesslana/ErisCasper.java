package com.github.princesslana.eriscasper;

import com.github.princesslana.eriscasper.event.Event;
import com.github.princesslana.eriscasper.repository.RepositoryDefinition;
import com.github.princesslana.eriscasper.repository.RepositoryManager;
import com.github.princesslana.eriscasper.rest.Route;
import com.github.princesslana.eriscasper.rest.Routes;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class BotContext {

  private final Flowable<Event<?>> events;

  private Routes routes;

  private RepositoryManager repositories;

  public BotContext(Flowable<Event<?>> events, Routes routes, RepositoryManager repositories) {
    this.events = events;
    this.routes = routes;
    this.repositories = repositories;
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

  public <R> R getRepository(RepositoryDefinition<R> def) {
    return repositories.get(def);
  }
}
