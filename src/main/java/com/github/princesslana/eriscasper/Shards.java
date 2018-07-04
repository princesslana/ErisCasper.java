package com.github.princesslana.eriscasper;

import com.github.princesslana.eriscasper.data.gateway.ShardPayload;
import com.google.common.base.Preconditions;
import com.ufoscout.properlty.Properlty;
import java.util.Optional;

public final class Shards {

  private Shards() {}

  public static Optional<ShardPayload> fromConfig(Properlty config) {
    Optional<Long> shard = config.getLong("ec.shard.id");
    Optional<Long> total = config.getLong("ec.shard.total");
    if (shard.isPresent() ^ total.isPresent()) {
      throw new ErisCasperFatalException(
          "Failed to resolve both sharding values when only one was provided.");
    }
    return shard.map(
        s -> {
          long t = total.get();
          check(s, t);
          return ShardPayload.of(s, t);
        });
  }

  public static void check(long shard, long total) {
    Preconditions.checkArgument(shard >= 0, "Shard number must be greater than or equal to 0.");
    Preconditions.checkArgument(total >= 1, "Shard total must be greater than or equal to 1.");
    Preconditions.checkState(shard < total, "Shard number must be less than the shard total.");
  }

  public static void check(ShardPayload payload) {
    check(payload.getShardId(), payload.getNumShards());
  }
}
