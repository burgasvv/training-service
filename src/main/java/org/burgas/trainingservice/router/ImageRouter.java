package org.burgas.trainingservice.router;

import org.burgas.trainingservice.dao.image.Image;
import org.burgas.trainingservice.dto.exception.ExceptionResponse;
import org.burgas.trainingservice.service.ImageServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Configuration
public class ImageRouter {

    @Bean
    public RouterFunction<ServerResponse> imageRouting(ImageServiceImpl imageService) {
        return RouterFunctions.route()
                .path("/api/v1/images", builder -> builder

                        .GET("/by-id", request -> {
                            UUID imageId = UUID.fromString(request.param("imageId").orElseThrow());
                            Image image = imageService.findEntity(imageId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.parseMediaType(image.getContentType()))
                                    .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
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
                )
                .build();
    }
}
