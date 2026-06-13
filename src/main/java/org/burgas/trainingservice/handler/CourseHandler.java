package org.burgas.trainingservice.handler;

import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dto.course.CourseRequest;
import org.burgas.trainingservice.dto.course.CourseResponse;
import org.burgas.trainingservice.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CourseHandler {

    private final CourseService courseService;

    public ServerResponse getAllCourses(ServerRequest ignoredServerRequest) {
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseService.findAll());
    }

    public ServerResponse getCourseById(ServerRequest serverRequest) {
        UUID courseId = UUID.fromString(serverRequest.param("courseId").orElseThrow());
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseService.findById(courseId));
    }

    public ServerResponse createCourse(ServerRequest serverRequest) throws ServletException, IOException {
        CourseRequest courseRequest = serverRequest.body(CourseRequest.class);
        CourseResponse courseResponse = courseService.create(courseRequest);
        return ServerResponse
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/courses/by-id?courseId=" + courseResponse.getId()))
                .build();
    }

    public ServerResponse updateCourse(ServerRequest serverRequest) throws ServletException, IOException {
        CourseRequest courseRequest = serverRequest.body(CourseRequest.class);
        CourseResponse courseResponse = courseService.update(courseRequest);
        return ServerResponse
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/courses/by-id?courseId=" + courseResponse.getId()))
                .build();
    }

    public ServerResponse deleteCourse(ServerRequest serverRequest) {
        UUID courseId = UUID.fromString(serverRequest.param("courseId").orElseThrow());
        courseService.delete(courseId);
        return ServerResponse.noContent().build();
    }
}
