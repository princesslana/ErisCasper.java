package com.github.princesslana.eriscasper.util;

import com.github.princesslana.eriscasper.ErisCasperFatalException;
import com.ufoscout.properlty.Properlty;
import com.ufoscout.properlty.reader.ProgrammaticPropertiesReader;
import com.ufoscout.properlty.reader.Properties;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ShardTest {

  @Test
  public void fromConfig_whenBothValuesPresent_shouldSucceed() {
    Assert.assertEquals(
        Shard.fromConfig(generateProperlty(Optional.of(2), Optional.of(3))).get().unwrap(),
        new Integer[] {2, 3});
  }

  @Test
  public void fromConfig_whenBothValuesEmpty_shouldBeEmpty() {
    Assert.assertFalse(
        Shard.fromConfig(generateProperlty(Optional.empty(), Optional.empty())).isPresent());
  }

  @Test
  public void fromConfig_whenOneValuePresent_shouldThrowException() {
    Assertions.assertThatThrownBy(
            () -> Shard.fromConfig(generateProperlty(Optional.empty(), Optional.of(3))))
        .isInstanceOf(ErisCasperFatalException.class)
        .hasMessageContaining("resolve");
    Assertions.assertThatThrownBy(
            () -> Shard.fromConfig(generateProperlty(Optional.of(2), Optional.empty())))
        .isInstanceOf(ErisCasperFatalException.class)
        .hasMessageContaining("resolve");
  }

  @Test
  public void fromConfig_whenShardHigherThanTotal_shouldThrowError() {
    Assertions.assertThatThrownBy(
            () -> Shard.fromConfig(generateProperlty(Optional.of(3), Optional.of(2))))
        .isInstanceOf(ErisCasperFatalException.class)
        .hasMessageContaining("create");
  }

  private Properlty generateProperlty(Optional<Integer> shardNumber, Optional<Integer> shardTotal) {
    ProgrammaticPropertiesReader reader = Properties.add("def", "def");
    shardNumber.ifPresent(shard -> reader.add("ec.shard.id", String.valueOf(shard)));
    shardTotal.ifPresent(total -> reader.add("ec.shard.total", String.valueOf(total)));
    return Properlty.builder().add(reader).build();
  }
}
