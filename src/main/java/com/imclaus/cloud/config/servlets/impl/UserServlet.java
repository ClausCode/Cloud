package com.imclaus.cloud.config.servlets.impl;

import com.imclaus.cloud.config.servlets.Servlet;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;

public class UserServlet implements Servlet {
    @Override
    public ServerHttpSecurity.AuthorizeExchangeSpec apply(ServerHttpSecurity.AuthorizeExchangeSpec spec) {
        return spec
                .pathMatchers(
                        HttpMethod.GET,
                        "/api/v1/user/mfa-code"
                ).authenticated()

                .pathMatchers(
                        HttpMethod.POST,
                        "/api/v1/user/change-password",
                        "/api/v1/user/change-name",
                        "/api/v1/user/switch-mfa"
                ).authenticated();
    }
}
