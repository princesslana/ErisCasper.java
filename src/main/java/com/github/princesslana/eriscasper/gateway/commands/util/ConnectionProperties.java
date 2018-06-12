package com.github.princesslana.eriscasper.gateway.commands.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/topics/gateway#identify-identify-connection-properties">
 *     https://discordapp.com/developers/docs/topics/gateway#identify-identify-connection-properties</a>
 */
@Value.Immutable
public interface ConnectionProperties {
  @JsonProperty("$os")
  String getOs();

  @JsonProperty("$browser")
  String getBrowser();

  @JsonProperty("$device")
  String getDevice();

  static ConnectionProperties ofDefault() {
    return ImmutableConnectionProperties.builder()
        .os(System.getProperty("os.name"))
        .browser("ErisCasper.java")
        .device("ErisCasper.java")
        .build();
  }
}
