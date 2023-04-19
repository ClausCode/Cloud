package com.imclaus.cloud.config.servlets;

import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;

public interface Servlet {
    AuthorizeExchangeSpec apply(AuthorizeExchangeSpec spec);
}
