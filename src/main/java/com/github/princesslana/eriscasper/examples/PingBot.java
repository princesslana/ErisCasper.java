package com.github.princesslana.eriscasper.examples;

import com.github.princesslana.eriscasper.Bot;
import com.github.princesslana.eriscasper.BotContext;
import com.github.princesslana.eriscasper.ErisCasper;
import com.github.princesslana.eriscasper.action.Actions;
import com.github.princesslana.eriscasper.data.Message;
import com.github.princesslana.eriscasper.event.MessageCreate;
import io.reactivex.Completable;

public class PingBot implements Bot {

  @Override
  public Completable apply(BotContext ctx) {
    return ctx.on(
        MessageCreate.class,
        e -> {
          Message recv = e.unwrap();

          if (recv.getContent().equals("+ping")) {
            return ctx.execute(Actions.sendMessage(recv.getChannelId(), "pong"));
          }

          return ctx.doNothing();
        });
  }

  public static void main(String[] args) {
    ErisCasper.create().run(new PingBot());
  }
}
