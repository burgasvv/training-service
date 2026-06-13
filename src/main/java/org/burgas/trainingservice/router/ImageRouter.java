package org.burgas.trainingservice.router;

import org.burgas.trainingservice.handler.ExceptionHandler;
import org.burgas.trainingservice.handler.ImageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class ImageRouter {

    @Bean
    public RouterFunction<ServerResponse> imageRouting(ImageHandler imageHandler, ExceptionHandler exceptionHandler) {
        return RouterFunctions.route()
                .path("/api/v1/images", builder -> builder
                        .GET("/by-id", imageHandler::getImageById)
                        .onError(Throwable.class, exceptionHandler::throwException)
                        .build()
                )
                .build();
    }
}
