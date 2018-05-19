package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Message;
import com.github.princesslana.eriscasper.rest.ChannelRoute;
import com.github.princesslana.eriscasper.rest.channel.CreateMessageRequest;

public class Actions {
  private Actions() {}

  public static Action<CreateMessageRequest, Message> sendMessage(Snowflake chan, String msg) {
    return ActionTuple.of(ChannelRoute.on(chan).createMessage(), CreateMessageRequest.ofText(msg));
  }

  public static Action<CreateMessageRequest, Message> sendMessage(
      Snowflake chan, CreateMessageRequest msg) {
    return ActionTuple.of(ChannelRoute.on(chan).createMessage(), msg);
  }
}
