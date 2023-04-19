package com.imclaus.cloud.controllers;

import com.imclaus.cloud.Utils;
import com.imclaus.cloud.data.ResponseCodes;
import com.imclaus.cloud.dto.request.UserSignInRequestDTO;
import com.imclaus.cloud.dto.request.UserSignUpRequestDTO;
import com.imclaus.cloud.models.AuthModel;
import com.imclaus.cloud.models.UserModel;
import com.imclaus.cloud.security.JwtUtil;
import com.imclaus.cloud.services.AuthService;
import com.imclaus.cloud.services.MFAService;
import com.imclaus.cloud.services.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final MFAService mfaService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, AuthService authService, MFAService mfaService, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authService = authService;
        this.mfaService = mfaService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(path = "sign-up")
    public Mono<ResponseEntity<?>> signUp(ServerHttpResponse response, @RequestBody UserSignUpRequestDTO signUpRequestDTO) {
        return Utils.getContext().flatMap(context -> userService.findByEmail(signUpRequestDTO.getEmail()).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalUser -> {
            if (optionalUser.isPresent()) {
                return Mono.just(ResponseCodes.BAD_REQUEST);
            }
            return userService.create(signUpRequestDTO).flatMap(user -> authService.addRefresh(user, context.getBrowserId(), response));
        }));
    }

    @PostMapping("sign-in")
    public Mono<ResponseEntity<?>> signIn(ServerHttpResponse response, @RequestBody UserSignInRequestDTO signInRequestDTO) {
        return Utils.getContext().flatMap(context -> userService.findByEmail(signInRequestDTO.getEmail()).defaultIfEmpty(new UserModel()).flatMap(user -> {
            if (user.getId() == null) return Mono.just(ResponseCodes.BAD_REQUEST);
            if (!passwordEncoder.matches(signInRequestDTO.getPassword(), user.getPassword()))
                return Mono.just(ResponseCodes.BAD_REQUEST);
            if (user.getTfa()) {
                if (signInRequestDTO.getCode() == null || !mfaService.verifyTotp(signInRequestDTO.getCode(), user.getSecret())) {
                    return Mono.just(ResponseCodes.LOCKED);
                }
            }
            return authService.addRefresh(user, context.getBrowserId(), response);
        }).onErrorReturn(ResponseCodes.BAD_REQUEST));
    }

    @PostMapping("refresh")
    public Mono<ResponseEntity<?>> refresh(ServerHttpResponse response, @CookieValue(name = "refresh") String refreshToken) {
        return Utils.getContext().flatMap(context -> {
            Long userId;
            String browserId = context.getBrowserId();

            if (refreshToken.isBlank()) {
                return Mono.just(ResponseCodes.LOCKED);
            }

            try {
                userId = jwtUtil.extractUserId(refreshToken);
            } catch (Exception exception) {
                userId = null;
                exception.printStackTrace();
            }

            if (userId != null && jwtUtil.validateExpiration(refreshToken)) {
                Claims claims = jwtUtil.extractClaims(refreshToken);

                if (!claims.get("browser", String.class).equals(browserId)) return Mono.just(ResponseCodes.LOCKED);
                return authService.findByUserIdAndBrowser(userId, browserId).defaultIfEmpty(new AuthModel()).flatMap(auth -> {
                    if (auth.getId() == null) return Mono.just(ResponseCodes.LOCKED);
                    if (auth.getUpdated().toEpochMilli() != claims.get("updated", Long.class))
                        return Mono.just(ResponseCodes.LOCKED);
                    return userService.findById(auth.getUserId()).flatMap(user -> authService.addRefresh(user, context.getBrowserId(), response));
                }).onErrorReturn(ResponseCodes.LOCKED);
            }
            return Mono.just(ResponseCodes.LOCKED);
        });
    }

    @PostMapping("logout")
    public Mono<ResponseEntity<?>> logout(ServerHttpResponse response, @CookieValue(name = "id") String browserId) {
        ResponseCookie clear = ResponseCookie.from("refresh", "").path("/").httpOnly(true).secure(true).maxAge(0).build();
        response.getCookies().set("refresh", clear);
        return Utils.getContext().flatMap(context -> authService.findByUserIdAndBrowser(context.getCurrentUser().getId(), browserId).flatMap(auth -> authService.delete(auth).thenReturn(ResponseEntity.ok("Success!"))));
    }
}
