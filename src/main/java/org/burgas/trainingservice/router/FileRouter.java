package org.burgas.trainingservice.router;

import org.burgas.trainingservice.handler.ExceptionHandler;
import org.burgas.trainingservice.handler.FileHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class FileRouter {

    @Bean
    public RouterFunction<ServerResponse> fileRouting(FileHandler fileHandler, ExceptionHandler exceptionHandler) {
        return RouterFunctions.route()
                .path("/api/v1/files", builder -> builder
                        .GET("/by-id", fileHandler::getFileById)
                        .onError(Throwable.class, exceptionHandler::throwException)
                        .build()
                )
                .build();
    }
}
