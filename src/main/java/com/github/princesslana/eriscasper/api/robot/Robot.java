package com.github.princesslana.eriscasper.api.robot;

import com.github.princesslana.eriscasper.Bot;
import com.github.princesslana.eriscasper.BotContext;
import com.github.princesslana.eriscasper.Bots;
import com.github.princesslana.eriscasper.ErisCasper;
import com.github.princesslana.eriscasper.data.Message;
import com.github.princesslana.eriscasper.event.MessageCreate;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class Robot implements Bot {

  private List<Bot> bots = new ArrayList<>();

  public Completable apply(BotContext bctx) {
    return Bots.merge(bots).apply(bctx);
  }

  public void hear(String regex, Function<RobotContext, Completable> f) {
    hear(Pattern.compile(regex), f);
  }

  public void hear(Pattern regex, Function<RobotContext, Completable> f) {
    listen(regex, f, "");
  }

  public void listen(String regex, Function<RobotContext, Completable> f) {
    listen(Pattern.compile(regex), f);
  }

  public void listen(Pattern regex, Function<RobotContext, Completable> f) {
    // TODO: Add more prefixes. e.g., name, which requires BotContext#getSelf
    listen(regex, f, "+");
  }

  private void listen(Pattern regex, Function<RobotContext, Completable> f, String prefix) {
    listen(regex, f, Single.just(prefix));
  }

  private void listen(Pattern regex, Function<RobotContext, Completable> f, Single<String> prefix) {
    Function<Message, Maybe<Message>> startsWithPrefix =
        m -> prefix.flatMapMaybe(p -> m.getContent().startsWith(p) ? Maybe.just(m) : Maybe.empty());

    BiFunction<BotContext, Message, Single<RobotContext>> toRobotContext =
        (bctx, m) ->
            prefix
                .map(p -> StringUtils.removeStart(m.getContent(), p))
                .map(c -> new RobotContext(bctx, regex.matcher(c), m));

    bots.add(
        bctx ->
            messages(bctx)
                .flatMapMaybe(startsWithPrefix)
                .flatMapSingle(msg -> toRobotContext.apply(bctx, msg))
                .filter(RobotContext::matches)
                .flatMapCompletable(f));
  }

  public void run() {
    run(ErisCasper.create());
  }

  public void run(ErisCasper ec) {
    ec.run(this);
  }

  private static Observable<Message> messages(BotContext bctx) {
    return bctx.getEvents()
        .ofType(MessageCreate.class)
        .map(MessageCreate::unwrap)
        .filter(m -> !m.getAuthor().isBot().orElse(false));
  }
}
