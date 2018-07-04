package com.github.princesslana.eriscasper.rx.websocket;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.notNull;
import static org.mockito.BDDMockito.then;

import io.reactivex.observers.TestObserver;
import java.util.Optional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestRxWebSocket {

  private static final String TEST_MESSAGE = "test message";
  private static final String TEST_WS_URL = "wss://test.url/";

  @Mock private OkHttpClient mockClient;

  @Mock private WebSocket mockWebSocket;

  private ArgumentCaptor<WebSocketListener> listenerCaptor;

  private RxWebSocket subject;

  @BeforeMethod
  public void mockito() {
    MockitoAnnotations.initMocks(this);
  }

  @BeforeMethod
  public void setupSubject() {
    MockitoAnnotations.initMocks(this);

    subject = new RxWebSocket(mockClient);

    listenerCaptor = ArgumentCaptor.forClass(WebSocketListener.class);

    given(mockClient.newWebSocket(notNull(), listenerCaptor.capture())).willReturn(mockWebSocket);
  }

  private WebSocketListener getListener() {
    return listenerCaptor.getValue();
  }

  @Test
  public void connect_shouldConnectToUrl() {
    ArgumentCaptor<Request> request = ArgumentCaptor.forClass(Request.class);

    given(mockClient.newWebSocket(request.capture(), notNull())).willReturn(null);

    subject.connect(TEST_WS_URL).subscribe();

    Assertions.assertThat(request.getValue().url().toString()).isEqualTo("https://test.url/");
  }

  @Test
  public void connect_whenStringMessage_shouldEmitEvent() {
    TestObserver<RxWebSocketEvent> subscriber = connect();
    getListener().onMessage(mockWebSocket, TEST_MESSAGE);
    subscriber.assertValues(StringMessageTuple.of(mockWebSocket, TEST_MESSAGE));
  }

  @Test
  public void connect_whenByteStringMessage_shouldEmitEvent() {
    ByteString byteString = ByteString.encodeUtf8(TEST_MESSAGE);
    TestObserver<RxWebSocketEvent> subscriber = connect();
    getListener().onMessage(mockWebSocket, byteString);
    subscriber.assertValues(ByteStringMessageTuple.of(mockWebSocket, byteString));
  }

  @Test
  public void connect_whenOnFailure_shouldEmitEventThenError() {
    Throwable error = new NullPointerException();
    TestObserver<RxWebSocketEvent> subscriber = connect();
    getListener().onFailure(mockWebSocket, error, null);
    subscriber.assertValues(FailureTuple.of(mockWebSocket, error, Optional.empty()));
    subscriber.assertError(error);
  }

  @Test
  public void connect_whenClosing_shouldEmitEventThenCloseWebSocket() {
    TestObserver<RxWebSocketEvent> subscriber = connect();
    getListener().onClosing(mockWebSocket, 0, "Closing...");
    subscriber.assertValues(ClosingTuple.of(mockWebSocket, 0, "Closing..."));
    then(mockWebSocket).should().close(1000, null);
  }

  @Test
  public void connect_whenClosed_shouldEmitEventThenComplete() {
    TestObserver<RxWebSocketEvent> subscriber = connect();
    getListener().onClosed(mockWebSocket, 0, "Closed.");
    subscriber.assertValues(ClosedTuple.of(mockWebSocket, 0, "Closed."));
    subscriber.assertComplete();
  }

  @Test
  public void send_whenBeforeConnect_shouldThrowNullPointerException() {
    subject.send(TEST_MESSAGE).test().assertError(NullPointerException.class);
  }

  @Test
  public void send_whenStringMessage_shouldSendToWebSocket() {
    subject.connect(TEST_WS_URL).subscribe();
    TestObserver<Void> subscriber = subject.send(TEST_MESSAGE).test();

    subscriber.assertComplete();
    then(mockWebSocket).should().send(TEST_MESSAGE);
  }

  private TestObserver<RxWebSocketEvent> connect() {
    return subject.connect(TEST_WS_URL).test();
  }
}
