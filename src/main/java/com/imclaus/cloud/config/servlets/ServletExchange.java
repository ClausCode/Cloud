package com.imclaus.cloud.config.servlets;

import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

public class ServletExchange {
    private AuthorizeExchangeSpec spec;

    public ServletExchange(AuthorizeExchangeSpec spec) {
        this.spec = spec;
    }

    public ServletExchange apply(Class<? extends Servlet> servletClass) {
        try {
            Servlet servlet = servletClass.getDeclaredConstructor().newInstance();
            this.spec = servlet.apply(spec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public SecurityWebFilterChain build() {
        return this.spec
                .anyExchange().authenticated()
                .and()
                .build();
    }
}
