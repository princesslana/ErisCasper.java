package com.github.princesslana.eriscasper.data;

import com.github.princesslana.eriscasper.data.resource.UserResource;

public class Users {
  private Users() {}

  public static String mentionByNickname(UserResource user) {
    return String.format("<@!%s>", user.getId().unwrap());
  }
}
