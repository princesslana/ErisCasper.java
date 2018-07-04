package com.github.princesslana.eriscasper.rest.guild;

import com.github.princesslana.eriscasper.data.resource.PartialChannel;
import com.google.common.collect.ImmutableList;
import javax.management.relation.Role;
import org.immutables.value.Value;

/**
 * @see <a href="https://discordapp.com/developers/docs/resources/guild#create-guild-json-params">
 *     https://discordapp.com/developers/docs/resources/guild#create-guild-json-params</a>
 */
@Value.Immutable
public interface GuildCreateRequest {
  String getName();

  String getRegion();

  String getIcon();

  Integer getVerificationLevel();

  Integer getDefaultMessageNotifications();

  ImmutableList<Role> getRoles();

  ImmutableList<PartialChannel> getChannels();
}
