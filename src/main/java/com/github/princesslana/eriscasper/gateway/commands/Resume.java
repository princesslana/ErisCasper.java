package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.gateway.ImmutablePayload;
import com.github.princesslana.eriscasper.gateway.OpCode;
import com.github.princesslana.eriscasper.gateway.Payload;
import com.github.princesslana.eriscasper.gateway.SequenceNumber;
import com.github.princesslana.eriscasper.gateway.SessionId;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#resume">
 *     https://discordapp.com/developers/docs/topics/gateway#resume</a>
 */
@Value.Immutable
public interface Resume extends GatewayCommand {
  BotToken getToken();

  @JsonProperty("session_id")
  SessionId getSessionId();

  SequenceNumber getSeq();

  @Override
  default Payload toPayload(ObjectMapper jackson) {
    return ImmutablePayload.builder().op(OpCode.RESUME).d(jackson.valueToTree(this)).build();
  }
}
