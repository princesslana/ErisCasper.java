package com.github.princesslana.eriscasper;

import com.github.princesslana.eriscasper.action.Action;
import com.github.princesslana.eriscasper.event.Event;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * A Bot is a function that converts events into actions.
 *
 * <p>We use the reactivex Function types (rather than Java's) for easier use with rxjava methods.
 */
public interface Bot extends Function<Flowable<Event<?>>, Flowable<Action<?>>> {

  /**
   * Override to not throw an Exception.
   *
   * @see Function#apply(Object)
   */
  Flowable<Action<?>> apply(Flowable<Event<?>> es);

  public static Bot fromConsumer(Consumer<Event<?>> c) {
    return es -> es.doOnNext(c).ignoreElements().toFlowable();
  }
}
