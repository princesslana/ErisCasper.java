package com.github.princesslana.eriscasper.rest.guild;

import org.immutables.value.Value;

/**
 * @see <a
 *     href="https://discordapp.com/developers/docs/resources/guild#modify-current-user-nick-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#modify-current-user-nick-json-params</a>
 */
@Value.Immutable
public interface ModifyCurrentNickRequest {
  String getNick();

  static ModifyCurrentNickRequest ofNick(String nick) {
    return ImmutableModifyCurrentNickRequest.builder().nick(nick).build();
  }
}
