package com.github.princesslana.eriscasper.gateway.commands;

import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.gateway.commands.util.ConnectionProperties;
import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#identify">
 *     https://discordapp.com/developers/docs/topics/gateway#identify</a>
 */
// TODO: This structure is not complete
@Value.Immutable
public interface Identify {
  BotToken getToken();

  Optional<Integer[]> shard();

  default ConnectionProperties getProperties() {
    return ConnectionProperties.ofDefault();
  }
}
