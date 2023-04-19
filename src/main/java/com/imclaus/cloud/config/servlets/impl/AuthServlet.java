package com.imclaus.cloud.config.servlets.impl;

import com.imclaus.cloud.config.servlets.Servlet;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;

public class AuthServlet implements Servlet {
    @Override
    public ServerHttpSecurity.AuthorizeExchangeSpec apply(ServerHttpSecurity.AuthorizeExchangeSpec spec) {
        return spec
                .pathMatchers(
                        HttpMethod.POST,
                        "/api/v1/auth/sign-in",
                        "/api/v1/auth/sign-up",
                        "/api/v1/auth/refresh"
                ).permitAll()
                .pathMatchers(
                        HttpMethod.POST,
                        "/api/v1/auth/logout"
                ).authenticated();
    }
}
