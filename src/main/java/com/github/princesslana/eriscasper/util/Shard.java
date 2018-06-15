package com.github.princesslana.eriscasper.util;

import com.github.princesslana.eriscasper.ErisCasperFatalException;
import com.github.princesslana.eriscasper.data.immutable.Wrapper;

public class Shard implements Wrapper<Integer[]> {

  private final int shardNumber;
  private final int shardTotal;

  public Shard(int shardNumber, int shardTotal) {
    if (shardNumber > shardTotal || shardTotal < 1 || shardNumber < 0) {
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
}
