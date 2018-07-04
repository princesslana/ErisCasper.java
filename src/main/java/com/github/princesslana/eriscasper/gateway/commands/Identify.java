package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.data.gateway.ShardPayload;
import com.github.princesslana.eriscasper.gateway.ImmutablePayload;
import com.github.princesslana.eriscasper.gateway.OpCode;
import com.github.princesslana.eriscasper.gateway.Payload;

import java.util.Optional;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/topics/gateway#identify">
 *     https://discordapp.com/developers/docs/topics/gateway#identify</a>
 */
// TODO: This structure is not complete
@JsonDeserialize(as = ImmutableIdentify.class)
@Value.Immutable
@Value.Enclosing
public interface Identify extends GatewayCommand {
  BotToken getToken();

  Optional<ShardPayload> shard();

  default ConnectionProperties getProperties() {
    return ConnectionProperties.ofDefault();
  }

  @Override
  default Payload toPayload(ObjectMapper jackson) {
    return ImmutablePayload.builder().op(OpCode.IDENTIFY).d(jackson.valueToTree(this)).build();
  }

  /**
   * @see <a
   *     href="https://discordapp.com/developers/docs/topics/gateway#identify-identify-connection-properties">
   *     https://discordapp.com/developers/docs/topics/gateway#identify-identify-connection-properties</a>
   */
  @Value.Immutable
  interface ConnectionProperties {
    @JsonProperty("$os")
    String getOs();

    @JsonProperty("$browser")
    String getBrowser();

    @JsonProperty("$device")
    String getDevice();

    static ConnectionProperties ofDefault() {
      return ImmutableIdentify.ConnectionProperties.builder()
          .os(System.getProperty("os.name"))
          .browser("ErisCasper.java")
          .device("ErisCasper.java")
          .build();
    }
  }
}
