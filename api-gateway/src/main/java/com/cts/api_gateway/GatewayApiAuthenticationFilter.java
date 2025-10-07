package com.cts.api_gateway;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayApiAuthenticationFilter implements GatewayFilter {

    private static final Logger logger = LoggerFactory.getLogger(GatewayApiAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RouterValidator routerValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            logger.info("Secured route detected: {}", request.getURI());

            if (this.isAuthMissing(request)) {
                logger.warn("Authorization header is missing for secured route.");
                return this.onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }

            final String token = this.getAuthHeader(request);

            // FIX: Use the robust validation method from JwtUtil
            if (jwtUtil.isInvalid(token)) {
                logger.warn("Invalid JWT token received.");
                return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
            }

            // If token is valid, populate headers for downstream services
            return populateRequestWithHeaders(exchange, chain, token);
        }

        logger.info("Open route detected: {}", request.getURI());
        return chain.filter(exchange);
    }

    /**
     * Extracts claims and adds them as headers to the request for downstream services.
     */
    private Mono<Void> populateRequestWithHeaders(ServerWebExchange exchange, GatewayFilterChain chain, String token) {
        try {
            Claims claims = jwtUtil.getAllClaimsFromToken(token);

            // FIX: Correctly extract userId, email, and roles
            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String roles = claims.get("ROLES", String.class);

            logger.info("Authenticated user -> ID: {}, Email: {}, Roles: {}", userId, email, roles);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)   // BEST PRACTICE: Pass the immutable user ID
                    .header("X-Email", email)      // Pass the email
                    .header("X-Roles", roles)      // Pass the roles
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            logger.error("Error while populating request headers: {}", e.getMessage());
            return this.onError(exchange, "Error processing token claims", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private String getAuthHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getOrEmpty("Authorization").get(0);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        // It's good practice to also write the error message to the response body if possible
        return response.setComplete();
    }
}