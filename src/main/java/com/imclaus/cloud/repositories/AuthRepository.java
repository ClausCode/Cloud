package com.imclaus.cloud.repositories;

import com.imclaus.cloud.models.AuthModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AuthRepository extends ReactiveCrudRepository<AuthModel, Long> {
    Mono<AuthModel> findByBrowser(String browser);
    Mono<AuthModel> findByUserIdAndBrowser(Long userId, String browser);
}
