package com.github.princesslana.eriscasper.action;

import io.reactivex.Completable;
import io.reactivex.functions.Function;

public interface Action extends Function<ActionContext, Completable> {}
