package com.github.princesslana.eriscasper.event;

import com.github.princesslana.eriscasper.data.GuildCreateData;
import com.github.princesslana.eriscasper.data.ReadyData;
import java.util.function.Function;

public enum EventType {
  GUILD_CREATE(GuildCreateData.class, ImmutableGuildCreate::of),
  READY(ReadyData.class, ImmutableReady::of);

  private final EventFactory<?> factory;

  private EventType(EventFactory<?> factory) {
    this.factory = factory;
  }

  private <T> EventType(Class<T> dataClass, Function<T, Event<T>> factory) {
    this(EventFactoryTuple.of(dataClass, factory));
  }

  public EventFactory<?> getFactory() {
    return factory;
  }
}
