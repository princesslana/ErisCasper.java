package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.data.resource.Activity;
import com.github.princesslana.eriscasper.gateway.ImmutablePayload;
import com.github.princesslana.eriscasper.gateway.OpCode;
import com.github.princesslana.eriscasper.gateway.Payload;
import io.reactivex.annotations.Nullable;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#update-status">
 *     https://discordapp.com/developers/docs/topics/gateway#update-status</a>
 */
@Value.Immutable
public interface UpdatePresence extends GatewayCommand {
  @Nullable
  Integer getSince();

  @Nullable
  Activity getGame();

  String getStatus();

  Boolean getAfk();

  @Override
  default Payload toPayload(ObjectMapper jackson) {
    return ImmutablePayload.builder().op(OpCode.STATUS_UPDATE).d(jackson.valueToTree(this)).build();
  }
}
