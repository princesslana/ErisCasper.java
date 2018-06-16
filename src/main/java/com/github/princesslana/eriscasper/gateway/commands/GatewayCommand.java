package com.github.princesslana.eriscasper.gateway.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.princesslana.eriscasper.gateway.Payload;

public interface GatewayCommand {
  Payload toPayload(ObjectMapper jackson);
}
