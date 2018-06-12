package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.gateway.SequenceNumber;
import com.github.princesslana.eriscasper.gateway.SessionId;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#resume">
 *     https://discordapp.com/developers/docs/topics/gateway#resume</a>
 */
@Value.Immutable
public interface Resume {
  BotToken getToken();

  @JsonProperty("session_id")
  SessionId getSessionId();

  SequenceNumber getSeq();
}
