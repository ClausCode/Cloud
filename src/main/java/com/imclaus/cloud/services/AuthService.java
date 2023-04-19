package com.imclaus.cloud.services;

import com.imclaus.cloud.dto.UserDTO;
import com.imclaus.cloud.dto.response.UserAuthResponseDTO;
import com.imclaus.cloud.models.AuthModel;
import com.imclaus.cloud.models.UserModel;
import com.imclaus.cloud.repositories.AuthRepository;
import com.imclaus.cloud.security.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final ModelMapper mapper;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(
            AuthRepository authRepository,
            ModelMapper mapper,
            JwtUtil jwtUtil
    ) {
        this.authRepository = authRepository;
        this.mapper = mapper;
        this.jwtUtil = jwtUtil;
    }

    public Mono<AuthModel> create(Long userId, String browser) {
        AuthModel model = new AuthModel();
        model.setUserId(userId);
        model.setBrowser(browser);

        Instant now = Instant.now();

        model.setCreated(now);
        model.setUpdated(now);

        return authRepository.save(model)
                .publishOn(Schedulers.boundedElastic())
                .doOnError(throwable ->
                        authRepository.findByBrowser(browser)
                                .flatMap(this::delete)
                                .block()
                ).flatMap(auth -> authRepository.save(model));
    }

    public Mono<AuthModel> findByUserIdAndBrowser(Long userId, String browser) {
        return authRepository.findByUserIdAndBrowser(
                userId, browser
        );
    }

    public Mono<Void> delete(AuthModel model) {
        return authRepository.delete(model);
    }

    public Mono<AuthModel> save(AuthModel model) {
        return authRepository.save(model);
    }

    public Mono<ResponseEntity<?>> addRefresh(UserModel user, String browserId, ServerHttpResponse response) {
        return findByUserIdAndBrowser(user.getId(), browserId)
                .defaultIfEmpty(new AuthModel())
                .flatMap(model -> {
                    if (model.getId() == null) return create(user.getId(), browserId);
                    else return Mono.just(model);
                })
                .flatMap(auth -> {
                    auth.setUpdated(Instant.now());
                    ResponseCookie cookie = ResponseCookie
                            .from("refresh", jwtUtil.generateRefreshToken(
                                    user.getId(), browserId, auth.getUpdated()
                            ))
                            .maxAge(Duration.ofDays(30))
                            .httpOnly(true)
                            .secure(true)
                            .path("/")
                            .build();
                    response.addCookie(cookie);
                    return save(auth)
                            .map(model -> ResponseEntity.ok(
                                    new UserAuthResponseDTO(
                                            jwtUtil.generateToken(user, browserId),
                                            mapper.map(user, UserDTO.class)
                                    )
                            ));
                });
    }
}
