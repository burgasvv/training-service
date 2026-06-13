package org.burgas.trainingservice.router;

import org.burgas.trainingservice.filter.ProjectFilter;
import org.burgas.trainingservice.handler.ExceptionHandler;
import org.burgas.trainingservice.handler.ProjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class ProjectRouter {

    @Bean
    public RouterFunction<ServerResponse> projectRouting(
            ProjectHandler projectHandler, ProjectFilter projectFilter, ExceptionHandler exceptionHandler
    ) {
        return RouterFunctions.route()
                .path("/api/v1/projects", builder -> builder
                        .filter(projectFilter)
                        .GET("/by-id", projectHandler::getProjectById)
                        .POST("/create", projectHandler::createProject)
                        .POST("/update", projectHandler::updateProject)
                        .DELETE("/delete", projectHandler::deleteProject)
                        .PUT("/upload-task", projectHandler::uploadTask)
                        .DELETE("/remove-task", projectHandler::removeTask)
                        .onError(Throwable.class, exceptionHandler::throwException)
                        .build()
                ).build();
    }
}
