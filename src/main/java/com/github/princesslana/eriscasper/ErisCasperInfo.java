package com.github.princesslana.eriscasper;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.princesslana.eriscasper.util.Jackson;
import java.io.IOException;
import java.io.InputStream;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableErisCasperInfo.class)
public interface ErisCasperInfo {

  String getVersion();

  String getUrl();

  static ErisCasperInfo load() {
    try (InputStream in = ErisCasperInfo.class.getResourceAsStream("info.json")) {
      return Jackson.newObjectMapper().readValue(in, ErisCasperInfo.class);
    } catch (IOException e) {
      throw new ErisCasperFatalException(e);
    }
  }
}
