package com.github.princesslana.eriscasper.rest.channel;

import com.github.princesslana.eriscasper.data.Snowflake;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class TestGetChannelMessagesRequest {

  @Test
  public void toQueryString_whenEmpty_shouldBeEmptyString() {
    Assertions.assertThat(ImmutableGetChannelMessagesRequest.builder().build().toQueryString())
        .isEqualTo("");
  }

  @Test
  public void toQueryString_whenSingleField_shouldMakeQueryString() {
    Assertions.assertThat(
            ImmutableGetChannelMessagesRequest.builder().limit(25).build().toQueryString())
        .isEqualTo("limit=25");
  }

  @Test
  public void toQueryString_whenAllFields_shouldMakeQueryString() {
    Snowflake s = Snowflake.of("123");
    Assertions.assertThat(
            ImmutableGetChannelMessagesRequest.builder()
                .around(s)
                .before(s)
                .after(s)
                .limit(25)
                .build()
                .toQueryString()
                .split("&"))
        .hasSize(4)
        .containsOnly("around=123", "before=123", "after=123", "limit=25");
  }
}
