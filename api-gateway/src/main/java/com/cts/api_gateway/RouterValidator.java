package com.cts.api_gateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher; // Import AntPathMatcher

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/users/register",
            "/api/users/login",
            "/api/forgotPassword/**", // Matches /api/forgotPassword and anything after it
            "/api/hotels/{id}",
            "/api/reviews/hotel/{hotelId}",
<<<<<<< HEAD
            "/api/reviews/hotel/{hotelId}/average-rating",
            "/api/hotels/search",
            "/api/hotels/cards/unique-stays",
            "/api/hotels/cards/top-deals"
=======
            "/api/reviews/hotel/{hotelId}/average-rating"// AntPathMatcher correctly handles this variable
>>>>>>> 6b108121c764c2eab2f8b9d384f795961519cbe1
    );

    // Instantiate AntPathMatcher
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    // Use the matcher to compare the endpoint pattern with the request path
                    .noneMatch(uri -> pathMatcher.match(uri, request.getURI().getPath()));
}