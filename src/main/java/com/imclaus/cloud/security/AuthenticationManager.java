package com.imclaus.cloud.security;

import com.imclaus.cloud.models.UserModel;
import com.imclaus.cloud.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public AuthenticationManager(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        Long userId;

        try {
            userId = jwtUtil.extractUserId(authToken);
        } catch (Exception exception) {
            userId = null;
            exception.printStackTrace();
        }

        if (userId != null && jwtUtil.validateExpiration(authToken)) {
            Mono<UserModel> model = userService
                    .findById(userId);

            return model.flatMap(user -> {
                if (!jwtUtil.validateLastUpdate(user, authToken)) return Mono.empty();
                return Mono.just(
                        new UsernamePasswordAuthenticationToken(
                                user.getId(),
                                null,
                                user.getAuthorities()
                        )
                );
            });
        } else {
            return Mono.empty();
        }
    }
}
