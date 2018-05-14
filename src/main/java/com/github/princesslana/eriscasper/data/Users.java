package com.github.princesslana.eriscasper.data;

import com.github.princesslana.eriscasper.data.resource.User;
import java.util.Optional;

public class Users {
  private Users() {}

  public static boolean isBot(Optional<User> user) {
    return user.flatMap(User::isBot).orElse(false);
  }

  public static String mention(User user) {
    return String.format("<@%s>", user.getId().unwrap());
  }

  public static String mentionByNickname(User user) {
    return String.format("<@!%s>", user.getId().unwrap());
  }
}
