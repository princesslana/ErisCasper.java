package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.gateway.OpCode;
import io.reactivex.Completable;
import org.immutables.value.Value;

@Value.Immutable
public abstract class GatewayAction<I> implements Action {

  public abstract OpCode getCode();

  public abstract I getData();

  @Override
  public Completable apply(ActionContext context) {
    return context.getGateway().execute(getCode(), getData());
  }

  static <I> GatewayAction<I> of(OpCode code, I data) {
    return ImmutableGatewayAction.<I>builder().code(code).data(data).build();
  }
}
