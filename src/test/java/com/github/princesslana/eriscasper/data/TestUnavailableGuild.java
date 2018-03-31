package com.github.princesslana.eriscasper.data;

import com.github.princesslana.eriscasper.data.resource.UnavailableGuildResource;
import java.util.Optional;
import org.testng.annotations.Test;

public class TestUnavailableGuild {

  /**
   * @see <a
   *     href="https://discordapp.com/developers/docs/resources/guild#unavailable-guild-object-example-unavailable-guild">
   *     https://discordapp.com/developers/docs/resources/guild#unavailable-guild-object-example-unavailable-guild</a>
   */
  @Test
  public void deserialize_whenExamplePayload_shouldDeseralize() {
    String payload = "{ \"id\": \"41771983423143937\", \"unavailable\": true }";

    DataAssert.thatFromJson(payload, UnavailableGuildResource.class)
        .hasFieldOrPropertyWithValue("id", Snowflake.of("41771983423143937"))
        .hasFieldOrPropertyWithValue("unavailable", Optional.of(true));
  }
}
