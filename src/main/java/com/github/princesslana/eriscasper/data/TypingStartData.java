package com.github.princesslana.eriscasper.data;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as=ImmutableTypingStartData.class)
public interface TypingStartData {
}
