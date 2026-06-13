package org.burgas.trainingservice.router;

import org.burgas.trainingservice.dto.course.CourseRequest;
import org.burgas.trainingservice.dto.course.CourseResponse;
import org.burgas.trainingservice.dto.exception.ExceptionResponse;
import org.burgas.trainingservice.handler.CourseHandler;
import org.burgas.trainingservice.handler.ExceptionHandler;
import org.burgas.trainingservice.service.CourseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.UUID;

@Configuration
public class CourseRouter {

    @Bean
    public RouterFunction<ServerResponse> courseRouting(CourseHandler courseHandler, ExceptionHandler exceptionHandler) {
        return RouterFunctions.route()
                .path("/api/v1/courses", builder -> builder
                        .GET("", courseHandler::getAllCourses)
                        .GET("/by-id", courseHandler::getCourseById)
                        .POST("/create", courseHandler::createCourse)
                        .POST("/update", courseHandler::updateCourse)
                        .DELETE("/delete", courseHandler::deleteCourse)
                        .onError(Throwable.class, exceptionHandler::throwException)
                        .build()
                ).build();
    }
}
