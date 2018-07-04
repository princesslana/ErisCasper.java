package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.AuditLogObject;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.rest.auditlog.GetGuildAuditLogRequest;

public class AuditLogRoute {

  private final Snowflake id;

  private AuditLogRoute(Snowflake id) {
    this.id = id;
  }

  /**
   * This route is not documented well in the Discord API documentation.
   *
   * @see <a href="https://discordapp.com/developers/docs/resources/audit-log#get-guild-audit-log">
   *     https://discordapp.com/developers/docs/resources/audit-log#get-guild-audit-log</a>
   */
  public Route<GetGuildAuditLogRequest, AuditLogObject> getLogs() {
    return Route.get(
        "/guilds/" + id.unwrap() + "/audit-logs",
        Route.queryString(GetGuildAuditLogRequest::toQueryString),
        Route.jsonResponse(AuditLogObject.class));
  }

  public static AuditLogRoute on(Snowflake id) {
    return new AuditLogRoute(id);
  }

  public static AuditLogRoute on(Guild guild) {
    return on(guild.getId());
  }
}
