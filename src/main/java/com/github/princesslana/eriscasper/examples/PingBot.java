package com.github.princesslana.eriscasper.examples;

import com.github.princesslana.eriscasper.Bot;
import com.github.princesslana.eriscasper.ErisCasper;

public class PingBot {

  public static void main(String[] args) {
    ErisCasper.create().run(Bot.fromConsumer(System.out::println));
  }
}
