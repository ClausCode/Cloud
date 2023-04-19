package com.imclaus.cloud.security;

import com.imclaus.cloud.Utils;
import com.imclaus.cloud.security.context.impl.CloudSecurityContextImpl;
import com.imclaus.cloud.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityContextRepository(
            AuthenticationManager authenticationManager,
            UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new IllegalStateException("Save method not supported!");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        String browserId = Utils.getBrowserId(exchange.getRequest(), exchange.getResponse());

        if (authHeader != null && authHeader.startsWith("Bearer_")) {
            String accessToken = authHeader.substring(7);
            if (!accessToken.isEmpty() && jwtUtil.validateBrowser(browserId, accessToken)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(accessToken, accessToken);
                return authenticationManager
                        .authenticate(auth)
                        .flatMap(authentication -> userService.findById((Long) authentication.getPrincipal())
                                .map(user -> {
                                    CloudSecurityContextImpl context = new CloudSecurityContextImpl(authentication);
                                    context.setCurrentUser(user);
                                    context.setBrowserId(browserId);

                                    return context;
                                }));
            }
        }

        CloudSecurityContextImpl context = new CloudSecurityContextImpl();
        context.setAuthentication(null);
        context.setCurrentUser(null);
        context.setBrowserId(browserId);

        return Mono.just(context);
    }
}
