package com.github.princesslana.eriscasper.api;

import com.github.princesslana.eriscasper.Bot;
import com.github.princesslana.eriscasper.BotContext;
import com.github.princesslana.eriscasper.Bots;
import com.github.princesslana.eriscasper.ErisCasper;
import com.github.princesslana.eriscasper.event.Event;
import com.github.princesslana.eriscasper.event.Events;
import io.reactivex.Completable;
import io.reactivex.functions.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Robot {

  private List<Bot> bots = new ArrayList<>();

  public void hear(String regex, Consumer<RobotContext> ctx) {
    hear(Pattern.compile(regex), ctx);
  }

  public void hear(Pattern regex, Consumer<RobotContext> ctx) {
    bots.add(new HearBot(regex, ctx));
  }

  public void run() {
    run(ErisCasper.create());
  }

  public void run(ErisCasper ec) {
    ec.run(Bots.merge(bots));
  }

  private static class HearBot implements Bot {

    private final Pattern regex;
    private final Consumer<RobotContext> f;

    public HearBot(Pattern regex, Consumer<RobotContext> f) {
      this.regex = regex;
      this.f = f;
    }

    public Completable apply(BotContext bctx) {
      return bctx.getEvents()
          .ofType(Events.MessageCreate.class)
          .map(Event::getData)
          .filter(m -> !m.getAuthor().isBot())
          .map(m -> new RobotContext(regex.matcher(m.getContent()), m))
          .filter(RobotContext::matches)
          .doOnNext(f::accept)
          .flatMapCompletable(rctx -> rctx.actions(bctx));
    }
  }
}
