package com.github.princesslana.eriscasper.rest.channel;

import com.github.princesslana.eriscasper.data.Snowflake;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/channel#get-channel-messages-query-string-params">
 *     https://discordapp.com/developers/docs/resources/channel#get-channel-messages-query-string-params</a>
 */
@Value.Immutable
public interface GetChannelMessagesRequest {
  Optional<Snowflake> getAround();

  Optional<Snowflake> getBefore();

  Optional<Snowflake> getAfter();

  Optional<Long> getLimit();

  default String toQueryString() {
    Function<String, Function<String, String>> encode = k -> v -> String.format("%s=%s", k, v);
    Function<String, Function<Snowflake, String>> encodeSnowflake =
        k -> v -> encode.apply(k).apply(v.unwrap());

    return Stream.of(
            getAround().map(encodeSnowflake.apply("around")),
            getBefore().map(encodeSnowflake.apply("before")),
            getAfter().map(encodeSnowflake.apply("after")),
            getLimit().map(l -> l.toString()).map(encode.apply("limit")))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.joining("&"));
  }
}
