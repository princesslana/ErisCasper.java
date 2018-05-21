package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import io.reactivex.Maybe;

public interface GuildRepository {
  Maybe<Guild> getGuild(Snowflake id);

  Maybe<Channel> getChannel(Snowflake id);

  Maybe<Guild> getGuildFromChannel(Snowflake channelId);
}
