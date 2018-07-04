package com.github.princesslana.eriscasper.action;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.request.CreateMessageRequest;
import com.github.princesslana.eriscasper.data.request.ImmutableCreateMessageRequest;
import com.github.princesslana.eriscasper.data.resource.Message;
import com.github.princesslana.eriscasper.rest.ChannelRoute;

public class Actions {
  private Actions() {}

  public static Action<CreateMessageRequest, Message> sendMessage(Snowflake chan, String msg) {
    return Action.of(
        ChannelRoute.on(chan).createMessage(),
        ImmutableCreateMessageRequest.builder().content(msg).build());
  }

  public static Action<CreateMessageRequest, Message> sendMessage(
      Snowflake chan, CreateMessageRequest msg) {
    return Action.of(ChannelRoute.on(chan).createMessage(), msg);
  }
}
