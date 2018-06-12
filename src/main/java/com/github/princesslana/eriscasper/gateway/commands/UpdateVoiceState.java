package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.princesslana.eriscasper.data.Snowflake;
import io.reactivex.annotations.Nullable;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#update-voice-state">
 *     https://discordapp.com/developers/docs/topics/gateway#update-voice-state</a>
 */
@Value.Immutable
public interface UpdateVoiceState {
  @JsonProperty("guild_id")
  Snowflake getGuildId();

  @JsonProperty("channel_id")
  @Nullable
  Snowflake getChannelId();

  @JsonProperty("self_mute")
  Boolean isMuted();

  @JsonProperty("self_deaf")
  Boolean isDeaf();

  static UpdateVoiceState disconnect(Snowflake guildId) {
    return connect(guildId, null);
  }

  static UpdateVoiceState connect(Snowflake guildId, Snowflake channelId) {
    return connect(guildId, channelId, false, false);
  }

  static UpdateVoiceState connect(
      Snowflake guildId, Snowflake channelId, boolean deaf, boolean mute) {
    return ImmutableUpdateVoiceState.builder()
        .guildId(guildId)
        .channelId(channelId)
        .isDeaf(deaf)
        .isMuted(mute)
        .build();
  }
}
