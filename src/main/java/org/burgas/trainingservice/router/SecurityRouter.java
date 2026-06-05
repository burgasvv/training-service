package org.burgas.trainingservice.router;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dto.exception.ExceptionResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityRouter {

    @Bean
    public RouterFunction<ServerResponse> securityRouting() {
        return RouterFunctions.route()
                .path("/api/v1/security", builder -> builder

                        .GET("/csrf-token", request -> {
                            CsrfToken csrfToken = (CsrfToken) request.attribute("_csrf").orElseThrow();
                            return ServerResponse.ok().body(csrfToken);
                        })

                        .GET("/login", request -> {
                            Authentication authentication = (Authentication) request.principal().orElseThrow();
                            if (authentication.isAuthenticated()) {
                                return ServerResponse.ok().body("You successfully authenticated");
                            } else {
                                throw new IllegalArgumentException("Not authenticated");
                            }
                        })

                        .GET("/logout", request -> {
                            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                            assert authentication != null;
                            if (authentication.isAuthenticated()) {
                                authentication.setAuthenticated(false);
                                SecurityContextHolder.clearContext();
                                HttpSession session = request.session();
                                session.invalidate();
                                return ServerResponse.ok().body("You successfully logged out");
                            } else {
                                return ServerResponse.ok().body("You already logged out");
                            }
                        })

                        .onError(Exception.class, (throwable, _) -> {
                            var exceptionResponse = ExceptionResponse.builder()
                                    .status(HttpStatus.BAD_REQUEST.name())
                                    .code(HttpStatus.BAD_REQUEST.value())
                                    .message(throwable.getLocalizedMessage())
                                    .build();
                            return ServerResponse.badRequest().body(exceptionResponse);
                        })
                        .build()

                ).build();
    }
}
