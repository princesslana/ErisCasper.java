package com.github.princesslana.eriscasper.rx;

import io.reactivex.Observable;
import java.util.Optional;

public class Observables {

  public static <T> Observable<T> fromNullable(T v) {
    return v == null ? Observable.empty() : Observable.just(v);
  }

  public static <T> Observable<T> fromOptional(Optional<T> op) {
    return op.map(Observable::just).orElse(Observable.empty());
  }
}
