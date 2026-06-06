package org.burgas.trainingservice.router;

import org.burgas.trainingservice.dto.exception.ExceptionResponse;
import org.burgas.trainingservice.dto.project.ProjectRequest;
import org.burgas.trainingservice.dto.project.ProjectResponse;
import org.burgas.trainingservice.service.ProjectService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.UUID;

@Configuration
public class ProjectRouter {

    @Bean
    public RouterFunction<ServerResponse> projectRouting(ProjectService projectService) {
        return RouterFunctions.route()
                .path("/api/v1/projects", builder -> builder

                        .GET("/by-id", request -> {
                            UUID projectId = UUID.fromString(request.param("projectId").orElseThrow());
                            return ServerResponse.ok().body(projectService.findById(projectId));
                        })

                        .POST("/create", request -> {
                            ProjectRequest projectRequest = request.body(ProjectRequest.class);
                            ProjectResponse projectResponse = projectService.create(projectRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/projects/by-id?projectId=" + projectResponse.getId()))
                                    .build();
                        })

                        .POST("/update", request -> {
                            ProjectRequest projectRequest = request.body(ProjectRequest.class);
                            ProjectResponse projectResponse = projectService.update(projectRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/projects/by-id?projectId=" + projectResponse.getId()))
                                    .build();
                        })

                        .DELETE("/delete", request -> {
                            UUID projectId = UUID.fromString(request.param("projectId").orElseThrow());
                            projectService.delete(projectId);
                            return ServerResponse.noContent().build();
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
