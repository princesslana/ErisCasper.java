package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.immutable.Tuple;
import org.immutables.value.Value;

@Value.Immutable
@Tuple
public interface RepositoryDefinition<R> {

  RepositoryDefinition<UserRepository> USER = RepositoryDefinitionTuple.of("user");
  RepositoryDefinition<GuildRepository> GUILD = RepositoryDefinitionTuple.of("guild");
  RepositoryDefinition<ChannelRepository> CHANNEL = RepositoryDefinitionTuple.of("channel");

  String getName();
}
