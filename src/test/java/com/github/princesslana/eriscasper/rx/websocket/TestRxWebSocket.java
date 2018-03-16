package com.github.princesslana.eriscasper.rx.websocket;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.notNull;

import io.reactivex.subscribers.TestSubscriber;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestRxWebSocket {

  private OkHttpClient mockClient;

  private RxWebSocket subject;

  @BeforeTest
  public void setupSubject() {
    mockClient = mock(OkHttpClient.class);

    subject = new RxWebSocket(mockClient);
  }

  @Test
  public void connect_connectsToUrl() {
    ArgumentCaptor<Request> request = ArgumentCaptor.forClass(Request.class);

    given(mockClient.newWebSocket(request.capture(), notNull())).willReturn(null);

    subject.connect("wss://test.url").subscribe();

    Assertions.assertThat(request.getValue().url().toString()).isEqualTo("https://test.url/");
  }

  @Test
  public void connect_whenStringMessage_emitsStringMessageEvent() {
    WebSocket mockWebSocket = mock(WebSocket.class);

    ArgumentCaptor<WebSocketListener> wsl = ArgumentCaptor.forClass(WebSocketListener.class);

    TestSubscriber<RxWebSocketEvent> subscriber = new TestSubscriber<>();

    given(mockClient.newWebSocket(notNull(), wsl.capture())).willReturn(mockWebSocket);

    subject.connect("wss://test.url").subscribe(subscriber);

    wsl.getValue().onMessage(mockWebSocket, "test message");

    subscriber.assertValuesOnly(StringMessageTuple.of(mockWebSocket, "test message"));
  }
}
