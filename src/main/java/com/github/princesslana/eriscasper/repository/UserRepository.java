package com.github.princesslana.eriscasper.repository;

import com.github.princesslana.eriscasper.data.resource.UserResource;
import io.reactivex.Single;

public interface UserRepository {
  Single<UserResource> getSelf();
}
