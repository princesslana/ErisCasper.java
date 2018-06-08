package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Message;
import com.github.princesslana.eriscasper.data.resource.User;
import com.github.princesslana.eriscasper.rest.channel.CreateMessageRequest;
import com.github.princesslana.eriscasper.rest.channel.EditMessageRequest;
import com.github.princesslana.eriscasper.rest.channel.GetChannelMessagesRequest;
import com.github.princesslana.eriscasper.rest.channel.GetReactionsRequest;
import com.github.princesslana.eriscasper.rest.channel.ModifyChannelRequest;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;

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
  public Route<GetChannelMessagesRequest, ImmutableList<Message>> getChannelMessages() {
    return Route.get(
        path("/"),
        (rq) -> FormTuple.of(rq.toQueryString(), ""),
        Route.jsonArrayResponse(Message.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#get-channel-message">
   *     https://discordapp.com/developers/docs/resources/channel#get-channel-message</a>
   */
  public Route<Void, Message> getChannelMessage(Snowflake messageId) {
    return Route.get(path("/messages/%s", messageId.unwrap()), Message.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#create-message">
   *     https://discordapp.com/developers/docs/resources/channel#create-message</a>
   */
  public Route<CreateMessageRequest, Message> createMessage() {
    return Route.post(path("/messages"), CreateMessageRequest.class, Message.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#create-reaction">
   *     https://discordapp.com/developers/docs/resources/channel#create-reaction</a>
   */
  public Route<Void, Void> createReaction(Snowflake messageId, String emoji) {
    return Route.put(
        path("/messages/%s/reactions/%s/@me", messageId.unwrap(), emoji), Void.class, Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#delete-own-reaction">
   *     https://discordapp.com/developers/docs/resources/channel#delete-own-reaction</a>
   */
  public Route<Void, Void> deleteOwnReaction(Snowflake messageId, String emoji) {
    return Route.delete(
        path("/messages/%s/reactions/%s/@me", messageId.unwrap(), emoji), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#delete-user-reaction">
   *     https://discordapp.com/developers/docs/resources/channel#delete-user-reaction</a>
   */
  public Route<Void, Void> deleteUserReaction(Snowflake messageId, String emoji, Snowflake userId) {
    return Route.delete(
        path("/messages/%s/reactions/%s/%s", messageId.unwrap(), emoji, userId.unwrap()),
        Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#get-reactions">
   *     https://discordapp.com/developers/docs/resources/channel#get-reactions</a>
   */
  public Route<GetReactionsRequest, ImmutableList<User>> getReactions(
      Snowflake messageId, String emoji) {
    return Route.get(
        path("/messages/%s/reactions/%s", messageId.unwrap(), emoji),
        (rq) -> FormTuple.of(rq.toQueryString(), ""),
        Route.jsonArrayResponse(User.class));
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#delete-all-reactions">
   *     https://discordapp.com/developers/docs/resources/channel#delete-all-reactions</a>
   */
  public Route<Void, Void> deleteAllReactions(Snowflake messageId) {
    return Route.delete(path("/messages/%s/reactions", messageId.unwrap()), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#edit-message">
   *     https://discordapp.com/developers/docs/resources/channel#edit-message</a>
   */
  public Route<EditMessageRequest, Message> editMessage(Snowflake messageId) {
    return Route.patch(
        path("/messages/%s", messageId.unwrap()), EditMessageRequest.class, Message.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#delete-message">
   *     https://discordapp.com/developers/docs/resources/channel#delete-message</a>
   */
  public Route<Void, Void> deleteMessage(Snowflake messageId) {
    return Route.delete(path("/messages/%s", messageId.unwrap()), Void.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/channel#bulk-delete-messages">
   *     https://discordapp.com/developers/docs/resources/channel#bulk-delete-messages</a>
   */
  public Route<ImmutableList<Snowflake>, Void> bulkDeleteMessages() {
    return Route.post(
        path("/messages/bulk-delete"), Route.<Snowflake>jsonArrayRequstBody(), Route.noResponse());
  }

  private String path(String fmt, String... args) {
    return "/channels/" + id.unwrap() + String.format(fmt, Arrays.asList(args).toArray());
  }

  public static ChannelRoute on(Snowflake id) {
    return new ChannelRoute(id);
  }

  public static ChannelRoute on(Channel channel) {
    return on(channel.getId());
  }
}
