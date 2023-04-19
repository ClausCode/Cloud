package com.imclaus.cloud.repositories;

import com.imclaus.cloud.models.UserModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserModel, Long> {
    Mono<UserModel> findByEmail(String email);
}
