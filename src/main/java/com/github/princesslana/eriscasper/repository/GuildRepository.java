package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

public interface GuildRepository {
  Maybe<Guild> getGuild(@NonNull Snowflake id);

  Single<ImmutableMap<Snowflake, Guild>> getGuilds();
}
