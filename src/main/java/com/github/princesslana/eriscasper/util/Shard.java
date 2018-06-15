package com.github.princesslana.eriscasper.util;

import com.github.princesslana.eriscasper.ErisCasperFatalException;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;
import com.ufoscout.properlty.Properlty;
import java.util.Optional;

public class Shard implements Wrapper<Integer[]> {

  private final int shardNumber;
  private final int shardTotal;

  public Shard(int shardNumber, int shardTotal) {
    if (shardNumber >= shardTotal || shardTotal < 1 || shardNumber < 0) {
      throw new ErisCasperFatalException(
          "Could not create sharding with [" + shardNumber + ", " + shardTotal + "]");
    }
    this.shardNumber = shardNumber;
    this.shardTotal = shardTotal;
  }

  @Override
  public Integer[] unwrap() {
    return new Integer[] {getShardNumber(), getShardTotal()};
  }

  public int getShardNumber() {
    return shardNumber;
  }

  public int getShardTotal() {
    return shardTotal;
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
