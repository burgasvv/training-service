package org.burgas.trainingservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.handler.ExceptionHandler;
import org.burgas.trainingservice.handler.SecurityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityRouter {

    @Bean
    public RouterFunction<ServerResponse> securityRouting(SecurityHandler securityHandler, ExceptionHandler exceptionHandler) {
        return RouterFunctions.route()
                .path("/api/v1/security", builder -> builder
                        .GET("/csrf-token", securityHandler::getCsrfToken)
                        .GET("/login", securityHandler::login)
                        .GET("/logout", securityHandler::logout)
                        .onError(Exception.class, exceptionHandler::throwException)
                        .build()
                ).build();
    }
}
