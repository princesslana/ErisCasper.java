package com.github.princesslana.eriscasper;

/**
 * A fatal error has occurred. Something has gone wrong that should never happen, or that ErisCasper
 * will not be able to recover from.
 */
public class ErisCasperFatalException extends RuntimeException {
  public ErisCasperFatalException() {
    super();
  }

  public ErisCasperFatalException(String msg) {
    super(msg);
  }

  public ErisCasperFatalException(Throwable cause) {
    super(cause);
  }

  public ErisCasperFatalException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
