package com.imclaus.cloud.controllers;

import com.imclaus.cloud.dto.UserDTO;
import com.imclaus.cloud.dto.request.*;
import com.imclaus.cloud.dto.response.UserAuthResponseDTO;
import com.imclaus.cloud.services.UserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AuthTest {
    @Autowired
    WebTestClient client;

    @Autowired
    UserService userService;

    public static String accessToken = "";
    public static String refreshToken = "";
    public static String browserId = "";

    @Test
    void t0_init() {
        assertTrue(true);
    }

    @Test
    void t1_signUp() {
        UserSignUpRequestDTO signUpRequestDTO = new UserSignUpRequestDTO();
        signUpRequestDTO.setEmail("test.user@gmail.com");
        signUpRequestDTO.setName("TestUser");
        signUpRequestDTO.setPassword("password");

        API.query(client, HttpMethod.POST, "auth/sign-up", signUpRequestDTO)
                .expectBody(UserAuthResponseDTO.class)
                .consumeWith(response -> {
                    UserAuthResponseDTO result = response.getResponseBody();

                    assertNotNull(result);
                    assertNotNull(result.getToken());

                    browserId = Objects.requireNonNull(response.getResponseCookies().getFirst("id")).getValue();
                    refreshToken = Objects.requireNonNull(response.getResponseCookies().getFirst("refresh")).getValue();

                    accessToken = result.getToken();
                });
    }

    @Test
    void t2_signIn() {
        UserSignInRequestDTO signInRequestDTO = new UserSignInRequestDTO();
        signInRequestDTO.setEmail("test.user@gmail.com");
        signInRequestDTO.setPassword("password");

        API.query(client, HttpMethod.POST, "auth/sign-in", signInRequestDTO)
                .expectBody(UserAuthResponseDTO.class)
                .consumeWith(response -> {
                    UserAuthResponseDTO result = response.getResponseBody();

                    assertNotNull(result);
                    assertNotNull(result.getToken());

                    refreshToken = Objects.requireNonNull(response.getResponseCookies().getFirst("refresh")).getValue();

                    accessToken = result.getToken();
                });
    }

    @Test
    void t3_refresh() {
        API.query(client, HttpMethod.POST, "auth/refresh", "")
                .expectBody(UserAuthResponseDTO.class)
                .consumeWith(response -> {
                    UserAuthResponseDTO result = response.getResponseBody();

                    assertNotNull(result);
                    assertNotNull(result.getToken());

                    refreshToken = Objects.requireNonNull(response.getResponseCookies().getFirst("refresh")).getValue();

                    accessToken = result.getToken();
                });
    }

    @Test
    void t4_changePassword() {
        UserChangePasswordRequestDTO changePasswordRequestDTO = new UserChangePasswordRequestDTO();
        changePasswordRequestDTO.setOldPassword("password");
        changePasswordRequestDTO.setNewPassword("new-password");

        API.query(client, HttpMethod.POST, "user/change-password", changePasswordRequestDTO)
                .expectBody(UserAuthResponseDTO.class)
                .consumeWith(response -> {
                    UserAuthResponseDTO result = response.getResponseBody();

                    assertNotNull(result);
                    assertNotNull(result.getToken());

                    refreshToken = Objects.requireNonNull(response.getResponseCookies().getFirst("refresh")).getValue();

                    accessToken = result.getToken();
                });
    }

    @Test
    void t5_signIn_newPassword() {
        UserSignInRequestDTO signInRequestDTO = new UserSignInRequestDTO();
        signInRequestDTO.setEmail("test.user@gmail.com");
        signInRequestDTO.setPassword("new-password");

        API.query(client, HttpMethod.POST, "auth/sign-in", signInRequestDTO)
                .expectBody(UserAuthResponseDTO.class)
                .consumeWith(response -> {
                    UserAuthResponseDTO result = response.getResponseBody();

                    assertNotNull(result);
                    assertNotNull(result.getToken());

                    refreshToken = Objects.requireNonNull(response.getResponseCookies().getFirst("refresh")).getValue();

                    accessToken = result.getToken();
                });
    }

    @Test
    void t6_changeName() {
        UserChangeNameRequestDTO changeNameRequestDTO = new UserChangeNameRequestDTO();
        changeNameRequestDTO.setName("New Test");

        API.query(client, HttpMethod.POST, "user/change-name", changeNameRequestDTO)
                .expectBody(UserDTO.class)
                .consumeWith(response -> {
                    UserDTO result = response.getResponseBody();

                    assertNotNull(result);
                    assertEquals(result.getName(), "New Test");
                });
    }
//
//    @Test
//    void t6_enableTFA() {
//        UserChangeTFARequestDTO changeTFARequestDTO = new UserChangeTFARequestDTO();
//        changeTFARequestDTO.setEnabled(true);
//
//        API.query(client, HttpMethod.POST, "user/change-tfa", changeTFARequestDTO)
//                .expectBody(UserDTO.class)
//                .consumeWith(response -> {
//                    UserDTO result = response.getResponseBody();
//
//                    assertNotNull(result);
//                    assertEquals(result.getName(), "New Test");
//                });
//    }

    @Test
    void t99_delete() {
        userService.findByEmail("test.user@gmail.com")
                .flatMap(user -> userService.delete(user))
                .block();
    }
}
