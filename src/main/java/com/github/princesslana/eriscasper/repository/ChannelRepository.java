package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.Channel;
import io.reactivex.Maybe;
import io.reactivex.Observable;

public interface ChannelRepository {
  Maybe<Channel> getChannel(Snowflake id);

  Observable<Channel> getChannels();
}
