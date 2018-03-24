package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.User;
import io.reactivex.Single;

public interface UserRepository {
  Single<User> getSelf();
}
