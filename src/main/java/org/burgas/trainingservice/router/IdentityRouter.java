package org.burgas.trainingservice.router;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dto.exception.ExceptionResponse;
import org.burgas.trainingservice.dto.file.FileRequest;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.burgas.trainingservice.dto.identity.IdentityResponse;
import org.burgas.trainingservice.service.IdentityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class IdentityRouter {

    @Bean
    public RouterFunction<ServerResponse> identityRouting(IdentityService identityService) {
        return RouterFunctions.route()
                .path("/api/v1/identities", builder -> builder

                        .GET("", _ -> ServerResponse.ok().body(identityService.findAll()))

                        .GET("/by-id", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            return ServerResponse.ok().body(identityService.findById(identityId));
                        })

                        .POST("/create", request -> {
                            IdentityRequest identityRequest = request.body(IdentityRequest.class);
                            identityService.create(identityRequest);
                            return ServerResponse.ok().build();
                        })

                        .POST("/update", request -> {
                            IdentityRequest identityRequest = request.body(IdentityRequest.class);
                            IdentityResponse identityResponse = identityService.update(identityRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/identities/by-id?identityId=" + identityResponse.getId()))
                                    .build();
                        })

                        .DELETE("/delete", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            identityService.delete(identityId);
                            return ServerResponse.noContent().build();
                        })

                        .POST("/upload-image", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            Part part = request.multipartData().getFirst("image");
                            assert part != null;
                            identityService.uploadImage(identityId, part);
                            return ServerResponse.ok().build();
                        })

                        .DELETE("/remove-image", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            identityService.removeImage(identityId);
                            return ServerResponse.ok().build();
                        })

                        .POST("/upload-files", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            List<Part> parts = request.multipartData().get("file");
                            identityService.uploadFiles(identityId, parts);
                            return ServerResponse.ok().build();
                        })

                        .DELETE("/remove-files", request -> {
                            UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());
                            FileRequest fileRequest = request.body(FileRequest.class);
                            identityService.removeFiles(identityId, fileRequest.getFileIds());
                            return ServerResponse.ok().build();
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
