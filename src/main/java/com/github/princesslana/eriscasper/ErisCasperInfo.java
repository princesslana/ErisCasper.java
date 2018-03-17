package com.github.princesslana.eriscasper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableErisCasperInfo.class)
public interface ErisCasperInfo {

  String getVersion();

  String getUrl();

  static ErisCasperInfo load() {
    ObjectMapper yaml = new ObjectMapper(new YAMLFactory());

    try (InputStream in = ErisCasperInfo.class.getResourceAsStream("info.yml")) {
      return yaml.readValue(in, ErisCasperInfo.class);
    } catch (IOException e) {
      throw new ErisCasperFatalException(e);
    }
  }
}
