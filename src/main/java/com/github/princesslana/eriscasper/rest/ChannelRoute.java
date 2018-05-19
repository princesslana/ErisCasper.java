package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Message;
import com.github.princesslana.eriscasper.rest.channel.CreateMessageRequest;
import com.github.princesslana.eriscasper.rest.channel.GetChannelMessagesRequest;
import com.github.princesslana.eriscasper.rest.channel.ModifyChannelRequest;
import com.google.common.collect.ImmutableList;

public class ChannelRoute {

  private final Snowflake id;

  private ChannelRoute(Snowflake id) {
    this.id = id;
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#get-channel">
   *     https://discordapp.com/developers/docs/resources/channel#get-channel</a>
   */
  public Route<Void, Channel> getChannel() {
    return Route.get(path("/"), Channel.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#modify-channel">
   *     https://discordapp.com/developers/docs/resources/channel#modify-channel</a>
   */
  public Route<ModifyChannelRequest, Channel> modifyChannel() {
    return Route.put(path("/"), ModifyChannelRequest.class, Channel.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#deleteclose-channel">
   *     https://discordapp.com/developers/docs/resources/channel#deleteclose-channel</a>
   */
  public Route<Void, Channel> deleteChannel() {
    return Route.delete(path("/"), Channel.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#get-channel-messages">
   *     https://discordapp.com/developers/docs/resources/channel#get-channel-messages</a>
   */
  public Route<GetChannelMessagesRequest, ImmutableList<Channel>> getChannelMessages() {
    return Route.get(path("/"), rq -> rq.toQueryString(), Route.jsonArrayResponse(Channel.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#get-channel-message">
   *     https://discordapp.com/developers/docs/resources/channel#get-channel-message</a>
   */
  public Route<Void, Message> getChannelMessage(Snowflake messageId) {
    return Route.get(path("/messages/" + messageId.unwrap()), Message.class);
  }

  public Route<CreateMessageRequest, Message> createMessage() {
    return Route.post(path("/messages"), CreateMessageRequest.class, Message.class);
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
