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
  Maybe<Channel> getChannel(Snowflake id);

  Observable<Channel> getChannels();
}
