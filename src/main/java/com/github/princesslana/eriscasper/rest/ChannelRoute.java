package com.github.princesslana.eriscasper.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.util.Jackson;
import com.github.princesslana.eriscasper.rest.channel.ModifyChannelRequest;
import com.google.common.collect.ImmutableList;

public class ChannelRoute {

  private final Snowflake id;

  private static final ObjectMapper JACKSON = Jackson.newObjectMapper();

  private ChannelRoute(Snowflake id) {
    this.id = id;
  }

  public Route<Void, Channel> getChannel() {
    return Route.get(path("/"), Channel.class);
  }

  public Route<ModifyChannelRequest, Channel> modifyChannel() {
    return Route.put(path("/"), ModifyChannelRequest.class, Channel.class);
  }

  public Route<Void, Channel> deleteChannel() {
    return Route.delete(path("/"), Channel.class);
  }

  @SuppressWarnings("unchecked")
  public Route<Void, ImmutableList<Channel>> getChannelMessages() {
    return Route.get(
        path("/"),
        rs ->
            (ImmutableList<Channel>)
                JACKSON.readValue(
                    rs.body().string(),
                    TypeFactory.defaultInstance()
                        .constructCollectionType(ImmutableList.class, Channel.class)));
  }

  private String path(String path) {
    return "/channels/" + id.unwrap() + path;
  }

  public static ChannelRoute on(Snowflake id) {
    return new ChannelRoute(id);
  }

  public static ChannelRoute on(Channel channel) {
    return on(channel.getId());
  }
}
