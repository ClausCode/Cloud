package com.imclaus.cloud.config;

import com.imclaus.cloud.config.servlets.ServletExchange;
import com.imclaus.cloud.config.servlets.impl.AuthServlet;
import com.imclaus.cloud.config.servlets.impl.DefaultServlet;
import com.imclaus.cloud.config.servlets.impl.UserServlet;
import com.imclaus.cloud.security.AuthenticationManager;
import com.imclaus.cloud.security.SecurityContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final WebFilter corsFilter;

    @Autowired
    public WebSecurityConfig(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            WebFilter corsFilter
    ) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.corsFilter = corsFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        AuthorizeExchangeSpec spec = httpSecurity
                .exceptionHandling()
                .authenticationEntryPoint(
                        (swe, e) ->
                                Mono.fromRunnable(
                                        () -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)
                                )
                )
                .accessDeniedHandler(
                        (swe, e) ->
                                Mono.fromRunnable(
                                        () -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)
                                )
                )
                .and()
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterAfter(corsFilter, SecurityWebFiltersOrder.CORS)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange();
        // Servlets
        return new ServletExchange(spec)
                .apply(DefaultServlet.class)
                .apply(AuthServlet.class)
                .apply(UserServlet.class)
                .build();
    }
}
