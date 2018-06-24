package com.github.princesslana.eriscasper.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.BotToken;
import com.github.princesslana.eriscasper.data.event.HelloEventData;
import com.github.princesslana.eriscasper.data.util.Jackson;
import java.io.IOException;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestPayloads {

  private final ObjectMapper jackson = Jackson.newObjectMapper();

  private Payloads subject;

  @BeforeMethod
  public void subject() {
    subject = new Payloads(jackson);
  }

  @Test
  public void identify_whenBotToken_shouldHaveIdentifyOpCode() {
    Payload p = subject.identify(BotToken.of("TEST_TOKEN"), Optional.empty());

    Assertions.assertThat(p.op()).isEqualTo(OpCode.IDENTIFY);
    Assertions.assertThat(p.d()).isPresent();
    Assertions.assertThat(p.d().get().get("token").asText()).isEqualTo("TEST_TOKEN");
  }

  @Test
  public void heartbeat_whenValidPayload_shouldDeserialize() throws IOException {
    String payload = "{\"heartbeat_interval\":41250,\"_trace\":[\"gateway-prd-main-wv81\"]}}";

    HelloEventData hb = jackson.readValue(payload, HelloEventData.class);

    Assertions.assertThat(hb.getHeartbeatInterval()).isEqualTo(41250);
    Assertions.assertThat(hb.getTrace()).containsOnly("gateway-prd-main-wv81");
  }

  @Test
  public void heartbeat_whenSequenceNumber_shouldIncludeIt() {
    SequenceNumber sn = SequenceNumber.of(123);

    Payload p = subject.heartbeat(Optional.of(sn));

    Assertions.assertThat(p.op()).isEqualTo(OpCode.HEARTBEAT);
    Assertions.assertThat(p.d()).isPresent();
    Assertions.assertThat(p.d().get().asInt()).isEqualTo(123);
  }

  @Test
  public void heartbeat_whenNoSequenceNumber_shouldHaveNullData() {
    Payload p = subject.heartbeat(Optional.empty());

    Assertions.assertThat(p.op()).isEqualTo(OpCode.HEARTBEAT);
    Assertions.assertThat(p.d().isPresent()).isFalse();
  }
}
