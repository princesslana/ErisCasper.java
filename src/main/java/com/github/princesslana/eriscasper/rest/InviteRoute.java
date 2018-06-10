package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.resource.Invite;
import java.util.Optional;

public class InviteRoute {

  private final String path;

  private InviteRoute(String code) {
    this.path = "/invites/" + code;
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/invite#get-invite">
   *     https://discordapp.com/developers/docs/resources/invite#get-invite</a>
   */
  public Route<Void, Invite> get(Optional<Boolean> withCounts) {
    return Route.get(
        path + withCounts.map(bool -> ";with_counts=" + bool).orElse(""), Invite.class);
  }

  /**
   * @see <a href="https://discordapp.com/developers/docs/resources/invite#delete-invite">
   *     https://discordapp.com/developers/docs/resources/invite#delete-invite</a>
   */
  public Route<Void, Invite> delete() {
    return Route.delete(path, Invite.class);
  }

  public static InviteRoute on(String code) {
    return new InviteRoute(code);
  }

  public static InviteRoute on(Invite invite) {
    return on(invite.getCode());
  }
}
