package com.github.princesslana.eriscasper.util;

import com.github.princesslana.eriscasper.ErisCasperFatalException;
import com.ufoscout.properlty.Properlty;
import com.ufoscout.properlty.reader.ProgrammaticPropertiesReader;
import com.ufoscout.properlty.reader.Properties;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class ShardTest {

  @Test
  public void fromConfig_whenBothValuesPresent_shouldSucceed() {
    Assertions.assertThat(
            Shard.fromConfig(
                properlty(Properties.add("ec.shard.id", "2").add("ec.shard.total", "3"))))
        .map(Shard::unwrap)
        .hasValue(new Integer[] {2, 3});
  }

  @Test
  public void fromConfig_whenBothValuesEmpty_shouldBeEmpty() {
    Assertions.assertThat(Shard.fromConfig(properlty())).isEmpty();
  }

  @Test
  public void fromConfig_whenOnlyIdPresent_shouldThrowException() {
    Assertions.assertThatThrownBy(
            () -> Shard.fromConfig(properlty(Properties.add("ec.shard.id", "2"))))
        .isInstanceOf(ErisCasperFatalException.class)
        .hasMessageContaining("resolve");
  }

  @Test
  public void fromConfig_whenOnlyTotalPresent_shouldThrowException() {
    Assertions.assertThatThrownBy(
            () -> Shard.fromConfig(properlty(Properties.add("ec.shard.total", "3"))))
        .isInstanceOf(ErisCasperFatalException.class)
        .hasMessageContaining("resolve");
  }

  @Test
  public void fromConfig_whenShardHigherThanTotal_shouldThrowError() {
    Assertions.assertThatThrownBy(
            () ->
                Shard.fromConfig(
                    properlty(Properties.add("ec.shard.id", "3").add("ec.shard.total", "2"))))
        .isInstanceOf(ErisCasperFatalException.class)
        .hasMessageContaining("create");
  }

  private Properlty properlty() {
    return Properlty.builder().build();
  }

  private Properlty properlty(ProgrammaticPropertiesReader reader) {
    return Properlty.builder().add(reader).build();
  }
}
