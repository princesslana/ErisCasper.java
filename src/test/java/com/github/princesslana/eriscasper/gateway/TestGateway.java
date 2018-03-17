package com.github.princesslana.eriscasper.gateway;

import okhttp3.OkHttpClient;
import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestGateway {

  @Mock private OkHttpClient mockClient;

  @Mock private Payloads mockPayloads;

  private Gateway subject;

  @BeforeMethod
  public void subject() {
    subject = new Gateway(mockClient, mockPayloads);
  }

  @Test
  public void close_whenBeforeConnect_shouldNotThrow() {
    Assertions.assertThatCode(() -> subject.close()).doesNotThrowAnyException();
  }
}
