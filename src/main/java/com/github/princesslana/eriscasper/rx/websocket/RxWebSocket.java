package com.github.princesslana.eriscasper.rx.websocket;

import com.github.princesslana.eriscasper.ErisCasperFatalException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.annotations.Nullable;
import java.util.Optional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxWebSocket {

  private static final Logger LOG = LoggerFactory.getLogger(RxWebSocket.class);

  private volatile boolean closed = false;
  private int exitCode = 0;
  private final OkHttpClient http;

  private WebSocket ws;

  public RxWebSocket(OkHttpClient http) {
    this.http = http;
  }

  public Observable<RxWebSocketEvent> connect(String url) {
    return Observable.<RxWebSocketEvent>create(
            em -> {
              Request rq = new Request.Builder().url(url).build();

              ws = http.newWebSocket(rq, new Listener(RxWebSocket.this, em));
            })
        .takeUntil(event -> closed)
        .doOnTerminate(() -> System.exit(exitCode))
        .doOnNext(e -> LOG.trace("Received: {}.", e))
        .doOnError(e -> LOG.warn("Error: {}.", e));
  }

  private void close0() {
    closed = true;
  }

  public void close(int code) {
    close(code, null);
  }

  public void close(int code, @Nullable String reason) {
    close0();
    ws.close(code, reason);
  }

  public Completable closeDueToInvalidSession() {
    return Completable.fromAction(
        () -> {
          exitCode = 1;
          close(1002, "Invalid session.");
        });
  }

  public Completable send(String text) {
    return Completable.fromAction(() -> ws.send(text))
        .doOnComplete(() -> LOG.trace("Sent: {}.", text));
  }

  private static class Listener extends WebSocketListener {
    private final ObservableEmitter<RxWebSocketEvent> em;
    private final RxWebSocket socket;

    private Listener(RxWebSocket socket, ObservableEmitter<RxWebSocketEvent> em) {
      this.socket = socket;
      this.em = em;
    }

    @Override
    public void onClosed(WebSocket ws, int code, String reason) {
      em.onNext(ClosedTuple.of(ws, code, reason));
      socket.close0();
    }

    @Override
    public void onClosing(WebSocket ws, int code, String reason) {
      if (!socket.closed) {
        if (code == 4004) {
          socket.close(1002, "Invalid token.");
          socket.exitCode = 1;
          new ErisCasperFatalException("Failed to authenticate with discord servers.")
              .fillInStackTrace()
              .printStackTrace();
        }
      }
      em.onNext(ClosingTuple.of(ws, code, reason));
    }

    @Override
    public void onFailure(WebSocket ws, Throwable t, Response response) {
      em.onNext(FailureTuple.of(ws, t, Optional.ofNullable(response)));
      em.onError(
          t instanceof ErisCasperFatalException
              ? t
              : new ErisCasperFatalException("Socket closed unexpectedly.", t));
    }

    @Override
    public void onMessage(WebSocket ws, okio.ByteString bytes) {
      em.onNext(ByteStringMessageTuple.of(ws, bytes));
    }

    @Override
    public void onMessage(WebSocket ws, String text) {
      em.onNext(StringMessageTuple.of(ws, text));
    }

    @Override
    public void onOpen(WebSocket ws, Response response) {
      em.onNext(OpenTuple.of(ws, response));
    }
  }
}
