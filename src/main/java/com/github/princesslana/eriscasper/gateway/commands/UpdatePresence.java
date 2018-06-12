package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.princesslana.eriscasper.data.resource.Activity;
import io.reactivex.annotations.Nullable;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#update-status">
 *     https://discordapp.com/developers/docs/topics/gateway#update-status</a>
 */
@Value.Immutable
public interface UpdatePresence {
  @JsonProperty("since")
  @Nullable
  Integer getSince();

  @JsonProperty("game")
  @Nullable
  Activity getGame();

  @JsonProperty("status")
  String getStatus();

  @JsonProperty("afk")
  Boolean getAfk();
}
