package com.github.princesslana.eriscasper;

import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class TestErisCasperInfo {

  @Test
  public void load_shouldNotThrow() {
    Assertions.assertThatCode(ErisCasperInfo::load).doesNotThrowAnyException();
  }
}
