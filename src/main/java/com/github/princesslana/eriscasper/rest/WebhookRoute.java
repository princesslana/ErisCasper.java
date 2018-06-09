package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Webhook;
import com.github.princesslana.eriscasper.rest.webhook.CreateWebhookRequest;
import com.github.princesslana.eriscasper.rest.webhook.ModifyWebhookRequest;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;

// TODO execute webhooks unfinished (needs multi-part params)
public class WebhookRoute {

  private final Snowflake id;

  private WebhookRoute(Snowflake id) {
    this.id = id;
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/webhook#get-webhook">
   *     https://discordapp.com/developers/docs/resources/webhook#get-webhook</a>
   */
  public Route<Void, Webhook> getWebhook() {
    return Route.get(path(""), Webhook.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/webhook#get-webhook-with-token">
   *     https://discordapp.com/developers/docs/resources/webhook#get-webhook-with-token</a>
   */
  public Route<Void, Webhook> getWebhookWithToken(String token) {
    return Route.get(path("/%s", token), Webhook.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/webhook#modify-webhook">
   *     https://discordapp.com/developers/docs/resources/webhook#modify-webhook</a>
   */
  public Route<ModifyWebhookRequest, Webhook> modifyWebhook() {
    return Route.patch(path(""), ModifyWebhookRequest.class, Webhook.class);
  }

  /**
   * @see <a
   *     href="https://discordapp.com/developers/docs/resources/webhook#modify-webhook-with-token">
   *     https://discordapp.com/developers/docs/resources/webhook#modify-webhook-with-token</a>
   */
  public Route<ModifyWebhookRequest, Webhook> modifyWebhookWithToken(String token) {
    return Route.patch(path("/%s", token), ModifyWebhookRequest.class, Webhook.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/webhook#delete-webhook">
   *     https://discordapp.com/developers/docs/resources/webhook#delete-webhook</a>
   */
  public Route<Void, Void> deleteWebhook() {
    return Route.delete(path(""), Void.class);
  }

  /**
   * @see <a
   *     href="https://discordapp.com/developers/docs/resources/webhook#delete-webhook-with-token">
   *     https://discordapp.com/developers/docs/resources/webhook#delete-webhook-with-token</a>
   */
  public Route<Void, Void> deleteWebhookWithToken(String token) {
    return Route.delete(path("/%s", token), Void.class);
  }

  private String path(String fmt, String... args) {
    return "/webhooks/" + id.unwrap() + String.format(fmt, Arrays.asList(args).toArray());
  }

  public static WebhookRoute on(Snowflake webhookId) {
    return new WebhookRoute(webhookId);
  }

  public static WebhookRoute on(Webhook webhook) {
    return on(webhook.getId());
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/webhook#create-webhook">
   *     https://discordapp.com/developers/docs/resources/webhook#create-webhook</a>
   */
  public static Route<CreateWebhookRequest, Webhook> createWebhook(Snowflake channelId) {
    return Route.post(cpath(channelId), CreateWebhookRequest.class, Webhook.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/webhook#get-channel-webhooks">
   *     https://discordapp.com/developers/docs/resources/webhook#get-channel-webhooks</a>
   */
  public static Route<Void, ImmutableList<Webhook>> getChannelWebhooks(Snowflake channelId) {
    return Route.get(cpath(channelId), Route.jsonArrayResponse(Webhook.class));
  }

  private static String cpath(Snowflake channelId) {
    return "/channels/" + channelId.unwrap() + "/webhooks";
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/webhook#get-guild-webhooks">
   *     https://discordapp.com/developers/docs/resources/webhook#get-guild-webhooks</a>
   */
  public static Route<Void, ImmutableList<Webhook>> getGuildWebhooks(Snowflake guildId) {
    return Route.get(
        "/guild/" + guildId.unwrap() + "/webhooks", Route.jsonArrayResponse(Webhook.class));
  }
}
