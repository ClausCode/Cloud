package com.imclaus.cloud.config.servlets.impl;

import com.imclaus.cloud.config.servlets.Servlet;
import org.springframework.security.config.web.server.ServerHttpSecurity;

public class DefaultServlet implements Servlet {
    @Override
    public ServerHttpSecurity.AuthorizeExchangeSpec apply(ServerHttpSecurity.AuthorizeExchangeSpec spec) {
        return spec
                .pathMatchers("/").permitAll()
                .pathMatchers("/favicon.ico").permitAll();
    }
}
