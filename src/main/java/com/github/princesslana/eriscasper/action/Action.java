package com.github.princesslana.eriscasper.action;

import io.reactivex.Completable;

public interface Action {

  Completable execute(ActionContext context);
}
