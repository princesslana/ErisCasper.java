package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Channel;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Predicate;

public interface ChannelRepository {
  Maybe<Channel> getChannel(@NonNull Snowflake id);

  Single<ImmutableMap<Snowflake, Channel>> getChannels();

  Maybe<Channel> getGuildCategoryFromName(Snowflake guildId, String name);

  Maybe<Channel> getGuildChannelFromName(
      Snowflake guildId, @Nullable Snowflake category, String name);

  Observable<Channel> getGuildCategories(Snowflake guildId);

  Observable<Channel> getGuildChannelsInCategory(Snowflake guildId, @Nullable Snowflake category);

  Observable<Channel> filter(Predicate<Channel> channelPredicate);

  Observable<Channel> filterGuild(Snowflake guildId, Predicate<Channel> channelPredicate);
}
