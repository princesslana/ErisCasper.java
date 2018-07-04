package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.gateway.OpCode;
import io.reactivex.Completable;
import org.immutables.value.Value;

@Value.Immutable
public interface GatewayAction<I> extends Action {

  OpCode getCode();

  // There is no gateway action which takes no data
  I getData();

  @Override
  default Completable execute(ActionContext context) {
    return context.getGateway().execute(getCode(), getData());
  }

  static <I> GatewayAction<I> of(OpCode code, I data) {
    return ImmutableGatewayAction.<I>builder().code(code).data(data).build();
  }
}
