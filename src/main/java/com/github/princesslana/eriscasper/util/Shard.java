package com.github.princesslana.eriscasper.util;

import com.fasterxml.jackson.annotation.JsonValue;
import com.github.princesslana.eriscasper.ErisCasperFatalException;
import com.google.common.base.Preconditions;
import com.ufoscout.properlty.Properlty;
import java.util.Optional;

public class Shard {

  private final int shardNumber;
  private final int shardTotal;

  public Shard(int shardNumber, int shardTotal) {
    Preconditions.checkArgument(
        shardNumber >= 0, "Shard number must be greater than or equal to 0.");
    Preconditions.checkArgument(shardTotal >= 1, "Shard total must be greater than or equal to 1.");
    Preconditions.checkState(
        shardNumber < shardTotal, "Shard number must be less than the shard total.");
    this.shardNumber = shardNumber;
    this.shardTotal = shardTotal;
  }

  @JsonValue
  public Integer[] toArray() {
    return new Integer[] {shardNumber, shardTotal};
  }

  public static Optional<Shard> fromConfig(Properlty config) {
    Optional<Integer> shard = config.getInt("ec.shard.id");
    Optional<Integer> total = config.getInt("ec.shard.total");
    if (shard.isPresent() || total.isPresent()) {
      ErisCasperFatalException exception =
          new ErisCasperFatalException(
              "Failed to resolve both sharding values when only one was provided.");
      return Optional.of(
          shard
              .map(s -> total.map(t -> new Shard(s, t)).orElseThrow(() -> exception))
              .orElseThrow(() -> exception));
    }
    return Optional.empty();
  }
}
