package com.github.princesslana.eriscasper.data;

import com.github.princesslana.eriscasper.data.resource.User;

public class Users {
  private Users() {}

  public static String mentionByNickname(User user) {
    return String.format("<@!%s>", user.getId().unwrap());
  }
}
