package com.github.princesslana.eriscasper;

import com.github.princesslana.eriscasper.action.Action;
import com.github.princesslana.eriscasper.data.event.Event;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import com.github.princesslana.eriscasper.gateway.commands.RequestGuildMembers;
import com.github.princesslana.eriscasper.gateway.commands.UpdatePresence;
import com.github.princesslana.eriscasper.gateway.commands.UpdateVoiceState;
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

  private final ErisCasper erisCasperInstance;
  private final Observable<Event> events;

  private Routes routes;

  private RepositoryManager repositories;

  public BotContext(
      ErisCasper erisCasperInstance,
      Observable<Event> events,
      Routes routes,
      RepositoryManager repositories) {
    this.erisCasperInstance = erisCasperInstance;
    this.events = events;
    this.routes = routes;
    this.repositories = repositories;
  }

  public Observable<Event> getEvents() {
    return events;
  }

  public <D, E extends Event & Wrapper<D>> Completable on(
      Class<E> evt, Function<D, Completable> f) {
    return events.ofType(evt).map(E::unwrap).flatMapCompletable(f::apply);
  }

  public Completable shutdownGracefully() {
    return Completable.fromAction(erisCasperInstance::shutdownGracefully);
  }

  public Completable requestGuildMembers(RequestGuildMembers request) {
    return erisCasperInstance.requestGuildMembers(request);
  }

  public Completable updatePresence(UpdatePresence update) {
    return erisCasperInstance.updatePresence(update);
  }

  public Completable updateVoiceState(UpdateVoiceState update) {
    return erisCasperInstance.updateVoiceState(update);
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

  public <Rs> Single<Rs> execute(Route<Void, Rs> route) {
    return routes.execute(route);
  }

  public <Rq, Rs> Single<Rs> execute(Route<Rq, Rs> route, Rq request) {
    return routes.execute(route, request);
  }

  private <Rq, Rs> Single<Rs> execute(Route<Rq, Rs> route, Optional<Rq> request) {
    return routes.execute(route, request.orElse(null));
  }

  public <R> R getRepository(RepositoryDefinition<R> def) {
    return repositories.get(def);
  }
}
