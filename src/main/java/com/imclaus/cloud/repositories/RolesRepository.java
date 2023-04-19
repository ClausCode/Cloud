package com.imclaus.cloud.repositories;

import com.imclaus.cloud.models.RoleModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolesRepository extends ReactiveCrudRepository<RoleModel, Long> {
    Mono<RoleModel> findByName(String name);

    @Query("SELECT * FROM roles where roles.id IN (SELECT user_roles.role_id from user_roles where user_roles.user_id = :userId)")
    Flux<RoleModel> findByUserId(@Param("userId") Long userId);

    @Query("INSERT INTO user_roles (user_id, role_id) values (:userId, :roleId)")
    Mono<Void> addByUserId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query("DELETE FROM user_roles where user_roles.user_id = :userId")
    Mono<Void> deleteByUserId(@Param("userId") Long userId);
}
