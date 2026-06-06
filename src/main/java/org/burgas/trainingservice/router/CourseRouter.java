package org.burgas.trainingservice.router;

import org.burgas.trainingservice.dto.course.CourseRequest;
import org.burgas.trainingservice.dto.course.CourseResponse;
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
    public RouterFunction<ServerResponse> courseRouting(CourseService courseService) {
        return RouterFunctions.route()
                .path("/api/v1/courses", builder -> builder

                        .GET("", _ -> ServerResponse.ok().body(courseService.findAll()))

                        .GET("/by-id", request -> {
                            UUID courseId = UUID.fromString(request.param("courseId").orElseThrow());
                            return ServerResponse.ok().body(courseService.findById(courseId));
                        })

                        .POST("/create", request -> {
                            CourseRequest courseRequest = request.body(CourseRequest.class);
                            CourseResponse courseResponse = courseService.create(courseRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/courses/by-id?courseId=" + courseResponse.getId()))
                                    .build();
                        })

                        .POST("/update", request -> {
                            CourseRequest courseRequest = request.body(CourseRequest.class);
                            CourseResponse courseResponse = courseService.update(courseRequest);
                            return ServerResponse
                                    .status(HttpStatus.FOUND)
                                    .location(URI.create("/api/v1/courses/by-id?courseId=" + courseResponse.getId()))
                                    .build();
                        })

                        .DELETE("/delete", request -> {
                            UUID courseId = UUID.fromString(request.param("courseId").orElseThrow());
                            courseService.delete(courseId);
                            return ServerResponse.noContent().build();
                        })

                        .build()
                ).build();
    }
}
