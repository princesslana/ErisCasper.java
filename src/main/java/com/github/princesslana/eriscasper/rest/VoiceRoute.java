package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.resource.VoiceRegion;
import com.google.common.collect.ImmutableList;

public final class VoiceRoute {
  private VoiceRoute() {}

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/voice#list-voice-regions">
   *     https://discordapp.com/developers/docs/resources/voice#list-voice-regions</a>
   */
  public static Route<Void, ImmutableList<VoiceRegion>> listVoiceRegions() {
    return Route.get("/voice/regions", Route.jsonArrayResponse(VoiceRegion.class));
  }
}
