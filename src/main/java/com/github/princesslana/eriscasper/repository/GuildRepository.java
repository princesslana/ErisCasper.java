package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.github.princesslana.eriscasper.data.resource.Guild;
import io.reactivex.Maybe;
import io.reactivex.annotations.NonNull;

public interface GuildRepository {
  Maybe<Guild> getGuild(@NonNull Snowflake id);

  Maybe<Channel> getChannel(@NonNull Snowflake id);
}
