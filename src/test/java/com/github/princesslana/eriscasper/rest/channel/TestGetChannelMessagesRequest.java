package com.github.princesslana.eriscasper.rest.channel;

import com.github.princesslana.eriscasper.data.Data;
import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.request.ImmutableGetChannelMessagesRequest;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class TestGetChannelMessagesRequest {

  @Test
  public void toQueryString_whenEmpty_shouldBeEmptyString() {
    Assertions.assertThat(Data.toQueryString(ImmutableGetChannelMessagesRequest.builder().build()))
        .isEqualTo("");
  }

  @Test
  public void toQueryString_whenSingleField_shouldMakeQueryString() {
    Assertions.assertThat(
            Data.toQueryString(ImmutableGetChannelMessagesRequest.builder().limit(25).build()))
        .isEqualTo("limit=25");
  }

  @Test
  public void toQueryString_whenAllFields_shouldMakeQueryString() {
    Snowflake s = Snowflake.of("123");
    Assertions.assertThat(
            Data.toQueryString(
                    ImmutableGetChannelMessagesRequest.builder()
                        .around(s)
                        .before(s)
                        .after(s)
                        .limit(25)
                        .build())
                .split("&"))
        .hasSize(4)
        .containsOnly("around=123", "before=123", "after=123", "limit=25");
  }
}
