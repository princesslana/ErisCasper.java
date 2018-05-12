package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Message;

public final class RouteCatalog {

  private RouteCatalog() {}

  public static Route<Void, GatewayResponse> getGateway() {
    return Route.get("/gateway", GatewayResponse.class);
  }

  public static Route<SendMessageRequest, Message> createMessage(Snowflake channelId) {
    return Route.post(
        String.format("/channels/%s/messages", channelId.unwrap()),
        SendMessageRequest.class,
        Message.class);
  }
}
