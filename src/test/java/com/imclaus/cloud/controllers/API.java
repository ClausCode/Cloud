package com.imclaus.cloud.controllers;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class API {
    private API() {}

    public static <T> WebTestClient.ResponseSpec query(WebTestClient client, HttpMethod method, String query, T body) {
        return client.method(method).uri("/api/v1/" + query)
                .header("Authorization", "Bearer_" + AuthTest.accessToken)
                .cookie("id", AuthTest.browserId)
                .cookie("refresh", AuthTest.refreshToken)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }
}
