package com.imclaus.cloud.controllers;

import com.imclaus.cloud.Utils;
import com.imclaus.cloud.data.ResponseCodes;
import com.imclaus.cloud.dto.UserDTO;
import com.imclaus.cloud.dto.request.UserChangeNameRequestDTO;
import com.imclaus.cloud.dto.request.UserChangePasswordRequestDTO;
import com.imclaus.cloud.dto.request.UserSignInRequestDTO;
import com.imclaus.cloud.dto.response.UserMFACodeResponseDTO;
import com.imclaus.cloud.models.UserModel;
import com.imclaus.cloud.services.AuthService;
import com.imclaus.cloud.services.MFAService;
import com.imclaus.cloud.services.UserService;
import dev.samstevens.totp.exceptions.QrGenerationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1/user/")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final MFAService mfaService;
    private final ModelMapper mapper;

    @Autowired
    public UserController(
            UserService userService,
            AuthService authService,
            MFAService mfaService, ModelMapper mapper
    ) {
        this.userService = userService;
        this.authService = authService;
        this.mfaService = mfaService;
        this.mapper = mapper;
    }

    @PostMapping("change-password")
    public Mono<ResponseEntity<?>> changePassword(
            ServerHttpResponse response,
            @RequestBody UserChangePasswordRequestDTO changePasswordRequestDTO
    ) {
        return Utils.getContext().flatMap(
                context -> {
                    try {
                        return userService.changePassword(
                                        context.getCurrentUser(),
                                        changePasswordRequestDTO
                                )
                                .flatMap(newUser -> authService.addRefresh(newUser, context.getBrowserId(), response));
                    } catch (Exception e) {
                        return Mono.just(ResponseCodes.BAD_REQUEST);
                    }
                }
        );
    }

    @PostMapping("change-name")
    public Mono<ResponseEntity<?>> changeName(
            @RequestBody UserChangeNameRequestDTO changeNameRequestDTO
    ) {
        return Utils.getContext().flatMap(
                context -> userService.changeName(
                                context.getCurrentUser(),
                                changeNameRequestDTO
                        )
                        .map(newUser -> ResponseEntity.ok(mapper.map(newUser, UserDTO.class)))
        );
    }

    @GetMapping("mfa-code")
    public Mono<ResponseEntity<?>> getMFACode() {
        return Utils.getContext().map(
                context -> {
                    try {
                        return ResponseEntity.ok(
                                new UserMFACodeResponseDTO(
                                        mfaService.getQRCode(context.getCurrentUser()),
                                        context.getCurrentUser().getSecret()
                                )
                        );
                    } catch (QrGenerationException e) {
                        return ResponseCodes.BAD_REQUEST;
                    }
                }
        );
    }

    @PostMapping("switch-mfa")
    public Mono<ResponseEntity<?>> switchMFA(
            @RequestBody UserSignInRequestDTO signInRequestDTO
    ) {
        return Utils.getContext().flatMap(
                context -> {
                    UserModel user = context.getCurrentUser();
                    if (!user.getTfa()) {
                        if (!mfaService.verifyTotp(signInRequestDTO.getCode(), user.getSecret())) {
                            return Mono.just(ResponseCodes.BAD_REQUEST);
                        }
                    }
                    return userService.switchMFA(
                                    context.getCurrentUser(),
                                    !user.getTfa()
                            )
                            .map(newUser -> ResponseEntity.ok(mapper.map(newUser, UserDTO.class)));
                }
        );
    }
}
