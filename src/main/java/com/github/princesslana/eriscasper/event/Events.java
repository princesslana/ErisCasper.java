package com.github.princesslana.eriscasper.event;

import com.github.princesslana.eriscasper.data.GuildCreateData;
import com.github.princesslana.eriscasper.data.ReadyData;
import org.immutables.value.Value;

public class Events {

  private Events() {}

  @Value.Immutable
  public static interface GuildCreate extends Event<GuildCreateData> {}

  @Value.Immutable
  public static interface Ready extends Event<ReadyData> {}
}
