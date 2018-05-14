package com.github.princesslana.eriscasper;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.princesslana.eriscasper.data.Data;
import com.github.princesslana.eriscasper.data.DataException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableErisCasperInfo.class)
public interface ErisCasperInfo {

  String getVersion();

  String getUrl();

  static ErisCasperInfo load() {
    URL url = ErisCasperInfo.class.getResource("info.json");

    try {
      String in = Resources.asByteSource(url).asCharSource(Charsets.UTF_8).read();

      return Data.fromJson(in, ErisCasperInfo.class);
    } catch (IOException | DataException e) {
      throw new ErisCasperFatalException(e);
    }
  }
}
