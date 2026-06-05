package org.burgas.trainingservice.router;

import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.dto.exception.ExceptionResponse;
import org.burgas.trainingservice.service.FileServiceImpl;
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
public class FileRouter {

    @Bean
    public RouterFunction<ServerResponse> fileRouting(FileServiceImpl fileService) {
        return RouterFunctions.route()
                .path("/api/v1/files", builder -> builder

                        .GET("/by-id", request -> {
                            UUID fileId = UUID.fromString(request.param("fileId").orElseThrow());
                            File file = fileService.findEntity(fileId);
                            return ServerResponse
                                    .status(HttpStatus.OK)
                                    .contentType(MediaType.parseMediaType(file.getContentType()))
                                    .body(new InputStreamResource(new ByteArrayInputStream(file.getData())));
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
