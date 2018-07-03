package com.github.princesslana.eriscasper;

import com.github.princesslana.eriscasper.action.Action;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import com.github.princesslana.eriscasper.repository.RepositoryDefinition;
import com.github.princesslana.eriscasper.repository.RepositoryManager;
import com.github.princesslana.eriscasper.rest.Route;
import com.github.princesslana.eriscasper.rest.Routes;
import com.google.common.base.Function;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotContext {

  private static final Logger LOG = LoggerFactory.getLogger(BotContext.class);

  private final Observable<Event> events;

  private Routes routes;

  private RepositoryManager repositories;

  public BotContext(Observable<Event> events, Routes routes, RepositoryManager repositories) {
    this.events = events;
    this.routes = routes;
    this.repositories = repositories;
  }

  public Observable<Event> getEvents() {
    return events;
  }

  public <D, E extends Event & Wrapper<D>> Completable on(
      Class<E> evt, Function<D, Completable> f) {
    // It would be nice to use a method reference here,
    // but doing so causes, an exception at runtime
    return events.ofType(evt).map(e -> e.unwrap()).flatMapCompletable(f::apply);
  }

  public Completable doNothing() {
    return Completable.complete();
  }

  /**
   * Executing an Action means we want to ignore the result.
   *
   * <p>(As opposed to a Query)
   */
  public <I, O> Completable execute(Action<I, O> action) {
    LOG.debug("Executing {}...", action);
    return execute(action.getRoute(), action.getData()).toCompletable();
  }

  public <O> Single<O> execute(Route<Void, O> route) {
    return routes.execute(route);
  }

  public <I, O> Single<O> execute(Route<I, O> route, I request) {
    return routes.execute(route, request);
  }

  private <I, O> Single<O> execute(Route<I, O> route, Optional<I> request) {
    return routes.execute(route, request.orElse(null));
  }

  public <R> R getRepository(RepositoryDefinition<R> def) {
    return repositories.get(def);
  }
}
