package com.github.princesslana.eriscasper.rest;

import com.github.princesslana.eriscasper.data.Snowflake;
import com.github.princesslana.eriscasper.data.resource.AuditLogObject;
import com.github.princesslana.eriscasper.data.resource.Guild;
import com.github.princesslana.eriscasper.rest.auditlog.GetGuildAuditLog;
import java.util.Arrays;

public class AuditLogRoute {
  // https://discordapp.com/developers/docs/resources/audit-log#get-guild-audit-log

  private final Snowflake id;

  private AuditLogRoute(Snowflake id) {
    this.id = id;
  }

  /**
   * Warning, this can and may throw an error. If it does please paste your console log into an
   * issue report. It will be fixed soon after. Thank you :)
   *
   * @see <a href="https://discordapp.com/developers/docs/resources/audit-log#get-guild-audit-log">
   *     https://discordapp.com/developers/docs/resources/audit-log#get-guild-audit-log</a>
   */
  public Route<GetGuildAuditLog, AuditLogObject> getLogs() {
    return Route.get(
        path(""),
        Route.queryString(GetGuildAuditLog::toQueryString),
        Route.jsonResponse(AuditLogObject.class));
  }

  private String path(String fmt, String... args) {
    return "/guilds/"
        + id.unwrap()
        + "/audit-logs"
        + String.format(fmt, Arrays.asList(args).toArray());
  }

  public static AuditLogRoute on(Snowflake id) {
    return new AuditLogRoute(id);
  }

  public static AuditLogRoute on(Guild guild) {
    return on(guild.getId());
  }
}
