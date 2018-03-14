package com.github.princesslana.eriscasper.data;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as=ImmutableMessage.class)
public interface Message {
}
