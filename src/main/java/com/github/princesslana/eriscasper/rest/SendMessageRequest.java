package com.github.princesslana.eriscasper.rest;

import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/resources/channel#create-message-params">
 *     https://discordapp.com/developers/docs/resources/channel#create-message-params</a>
 */
@Value.Immutable
public interface SendMessageRequest {
  String getContent();
}
