package org.burgas.trainingservice.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dto.project.ProjectRequest;
import org.burgas.trainingservice.dto.project.ProjectResponse;
import org.burgas.trainingservice.service.ProjectService;
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
public class ProjectHandler {

    private final ProjectService projectService;

    public ServerResponse getProjectById(ServerRequest serverRequest) {
        UUID projectId = UUID.fromString(serverRequest.param("projectId").orElseThrow());
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(projectService.findById(projectId));
    }

    public ServerResponse createProject(ServerRequest serverRequest) throws ServletException, IOException {
        ProjectRequest projectRequest = serverRequest.body(ProjectRequest.class);
        ProjectResponse projectResponse = projectService.create(projectRequest);
        return ServerResponse
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/projects/by-id?projectId=" + projectResponse.getId()))
                .build();
    }

    public ServerResponse updateProject(ServerRequest serverRequest) throws ServletException, IOException {
        ProjectRequest projectRequest = serverRequest.body(ProjectRequest.class);
        ProjectResponse projectResponse = projectService.update(projectRequest);
        return ServerResponse
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/projects/by-id?projectId=" + projectResponse.getId()))
                .build();
    }

    public ServerResponse deleteProject(ServerRequest serverRequest) {
        UUID projectId = UUID.fromString(serverRequest.param("projectId").orElseThrow());
        projectService.delete(projectId);
        return ServerResponse.noContent().build();
    }

    public ServerResponse uploadTask(ServerRequest serverRequest) throws ServletException, IOException {
        UUID projectId = UUID.fromString(serverRequest.param("projectId").orElseThrow());
        Part part = serverRequest.multipartData().getFirst("task");
        projectService.addTask(projectId, part);
        return ServerResponse.ok().build();
    }

    public ServerResponse removeTask(ServerRequest serverRequest) {
        UUID projectId = UUID.fromString(serverRequest.param("projectId").orElseThrow());
        projectService.removeTask(projectId);
        return ServerResponse.ok().build();
    }
}
