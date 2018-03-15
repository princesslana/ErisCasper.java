package com.github.princesslana.eriscasper;

import io.reactivex.Completable;
import io.reactivex.functions.Function;

/**
 * A Bot is a function that converts events into actions.
 *
 * <p>We use the reactivex Function types (rather than Java's) for easier use with rxjava methods.
 */
public interface Bot extends Function<BotContext, Completable> {

  /**
   * Override to not throw an Exception.
   *
   * @see Function#apply(Object)
   */
  Completable apply(BotContext ctx);
}
