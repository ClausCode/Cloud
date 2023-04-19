package com.imclaus.cloud;

import com.imclaus.cloud.security.context.CloudSecurityContext;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Utils {
    public static String getBrowserId(ServerHttpRequest request, ServerHttpResponse response) {
        Optional<HttpCookie> requestCookie = request.getCookies()
                .values().stream()
                .flatMap(List::stream)
                .filter(httpCookie -> httpCookie.getName().equals("id"))
                .findFirst();

        if (requestCookie.isPresent()) {
            String value = requestCookie.get().getValue();
            if (value.length() == 36) {
                return value;
            }
        }

        String browserId = UUID.randomUUID().toString();
        response.addCookie(
                ResponseCookie
                        .from("id", browserId)
                        .secure(true)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(Duration.ofDays(18250))
                        .build()
        );
        return browserId;
    }

    public static Mono<CloudSecurityContext> getContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> (CloudSecurityContext) context);
    }
}
