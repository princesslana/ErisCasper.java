package com.github.princesslana.eriscasper.util;

import com.github.princesslana.eriscasper.ErisCasper;
import com.github.princesslana.eriscasper.ErisCasperFatalException;
import com.github.princesslana.eriscasper.data.gateway.ShardPayload;
import com.google.common.collect.ImmutableList;
import com.ufoscout.properlty.Properlty;
import com.ufoscout.properlty.reader.ProgrammaticPropertiesReader;
import com.ufoscout.properlty.reader.Properties;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class TestShard {

  @Test
  public void fromConfig_whenBothValuesPresent_shouldSucceed() {
    Assertions.assertThat(
            ErisCasper.shardFromConfig(
                properlty(Properties.add("ec.shard.id", "2").add("ec.shard.total", "3"))))
        .map(ShardPayload::toArray)
        .hasValue(ImmutableList.of(2L, 3L));
  }

  @Test
  public void fromConfig_whenBothValuesEmpty_shouldBeEmpty() {
    Assertions.assertThat(ErisCasper.shardFromConfig(properlty())).isEmpty();
  }

  @Test
  public void fromConfig_whenOnlyIdPresent_shouldThrowException() {
    Assertions.assertThatThrownBy(
            () -> ErisCasper.shardFromConfig(properlty(Properties.add("ec.shard.id", "2"))))
        .isInstanceOf(ErisCasperFatalException.class)
        .hasMessageContaining("resolve");
  }

  @Test
  public void fromConfig_whenOnlyTotalPresent_shouldThrowException() {
    Assertions.assertThatThrownBy(
            () -> ErisCasper.shardFromConfig(properlty(Properties.add("ec.shard.total", "3"))))
        .isInstanceOf(ErisCasperFatalException.class)
        .hasMessageContaining("resolve");
  }

  @Test
  public void fromConfig_whenIdLessThan0_shouldThrowError() {
    Assertions.assertThatThrownBy(
            () ->
                ErisCasper.shardFromConfig(
                    properlty(Properties.add("ec.shard.id", "-1").add("ec.shard.total", "1"))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Shard number must be greater than or equal to 0.");
  }

  @Test
  public void fromConfig_whenTotalLessThan1_shouldThrowError() {
    Assertions.assertThatThrownBy(
            () ->
                ErisCasper.shardFromConfig(
                    properlty(Properties.add("ec.shard.id", "0").add("ec.shard.total", "0"))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Shard total must be greater than or equal to 1.");
  }

  @Test
  public void fromConfig_whenShardHigherThanTotal_shouldThrowError() {
    Assertions.assertThatThrownBy(
            () ->
                ErisCasper.shardFromConfig(
                    properlty(Properties.add("ec.shard.id", "3").add("ec.shard.total", "2"))))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Shard number must be less than the shard total.");
  }

  private Properlty properlty() {
    return Properlty.builder().build();
  }

  private Properlty properlty(ProgrammaticPropertiesReader reader) {
    return Properlty.builder().add(reader).build();
  }
}
