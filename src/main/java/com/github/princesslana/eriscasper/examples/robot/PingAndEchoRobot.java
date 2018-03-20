package com.github.princesslana.eriscasper.examples.robot;

import com.github.princesslana.eriscasper.api.Robot;

public class PingAndEchoRobot {

  public static void main(String[] args) {
    Robot robot = new Robot();

    robot.hear("\\+ping", ctx -> ctx.send("pong"));
    robot.hear("\\+echo (.+)", ctx -> ctx.send(ctx.match(1)));

    robot.run();
  }
}
