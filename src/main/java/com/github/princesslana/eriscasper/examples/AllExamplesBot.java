package com.github.princesslana.eriscasper.examples;

import com.github.princesslana.eriscasper.Bots;
import com.github.princesslana.eriscasper.ErisCasper;
import java.util.Arrays;

public class AllExamplesBot {

  public static void main(String args[]) {
    ErisCasper.create()
        .run(Bots.merge(Arrays.asList(new EchoBot(), new PingBot(), new WhoAreYouBot())));
  }
}
