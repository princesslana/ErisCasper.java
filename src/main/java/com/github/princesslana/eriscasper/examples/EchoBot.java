package com.github.princesslana.eriscasper.examples;

import com.github.princesslana.eriscasper.ErisCasper;
import com.github.princesslana.eriscasper.data.Users;
import com.github.princesslana.eriscasper.data.event.MessageCreateEvent;
import com.github.princesslana.eriscasper.data.request.ImmutableCreateMessageRequest;
import com.github.princesslana.eriscasper.rest.ChannelRoute;

public class EchoBot {
  public static void main(String args[]) {
    ErisCasper.create()
        .run(
            ctx ->
                ctx.getEvents()

                    // Same type as PingBot in examples
                    .ofType(MessageCreateEvent.class)
                    .map(MessageCreateEvent::unwrap)

                    // Need to check for bot's own message
                    .filter(d -> !Users.isBot(d.getAuthor()))
                    .filter(d -> d.getContent().map(c -> c.startsWith("+echo")).orElse(false))
                    .flatMapCompletable(
                        d -> {
                          String replyMessage =
                              d.getContent().map(c -> c.replaceFirst("\\+echo", "")).orElse("");

                          // Empty and Invalid Arguments
                          if (replyMessage.trim().isEmpty()) {
                            replyMessage = "This command requires 1 argument";
                          } else if (replyMessage.charAt(0) != ' ') {
                            replyMessage = "Invalid Command";
                          }

                          return ctx.execute(
                                  ChannelRoute.on(d.getChannelId()).createMessage(),
                                  ImmutableCreateMessageRequest.builder()
                                      .content(replyMessage)
                                      .build())
                              .toCompletable();
                        }));
  }
}
