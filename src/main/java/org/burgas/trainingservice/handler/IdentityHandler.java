package org.burgas.trainingservice.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dto.file.FileRequest;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.burgas.trainingservice.dto.identity.IdentityResponse;
import org.burgas.trainingservice.service.IdentityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdentityHandler {

    private final IdentityService identityService;

    public ServerResponse getAllIdentities(ServerRequest ignoredServerRequest) {
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(identityService.findAll());
    }

    public ServerResponse getIdentityById(ServerRequest serverRequest) {
        UUID identityId = UUID.fromString(serverRequest.param("identityId").orElseThrow());
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(identityService.findById(identityId));
    }

    public ServerResponse createIdentity(ServerRequest serverRequest) throws ServletException, IOException {
        IdentityRequest identityRequest = serverRequest.body(IdentityRequest.class);
        identityService.create(identityRequest);
        return ServerResponse.ok().build();
    }

    public ServerResponse updateIdentity(ServerRequest serverRequest) {
        IdentityRequest identityRequest = (IdentityRequest) serverRequest.attribute("identityRequest").orElseThrow();
        IdentityResponse identityResponse = identityService.update(identityRequest);
        return ServerResponse
                .status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/identities/by-id?identityId=" + identityResponse.getId()))
                .build();
    }

    public ServerResponse deleteIdentity(ServerRequest serverRequest) {
        UUID identityId = UUID.fromString(serverRequest.param("identityId").orElseThrow());
        identityService.delete(identityId);
        return ServerResponse.status(HttpStatus.FOUND)
                .location(URI.create("/api/v1/security/logout"))
                .build();
    }

    public ServerResponse uploadImage(ServerRequest serverRequest) throws ServletException, IOException {
        UUID identityId = UUID.fromString(serverRequest.param("identityId").orElseThrow());
        Part part = serverRequest.multipartData().getFirst("image");
        assert part != null;
        identityService.uploadImage(identityId, part);
        return ServerResponse.ok().build();
    }

    public ServerResponse removeImage(ServerRequest serverRequest) {
        UUID identityId = UUID.fromString(serverRequest.param("identityId").orElseThrow());
        identityService.removeImage(identityId);
        return ServerResponse.ok().build();
    }

    public ServerResponse uploadFiles(ServerRequest serverRequest) throws ServletException, IOException {
        UUID identityId = UUID.fromString(serverRequest.param("identityId").orElseThrow());
        List<Part> parts = serverRequest.multipartData().get("file");
        identityService.uploadFiles(identityId, parts);
        return ServerResponse.ok().build();
    }

    public ServerResponse removeFiles(ServerRequest serverRequest) throws ServletException, IOException {
        UUID identityId = UUID.fromString(serverRequest.param("identityId").orElseThrow());
        FileRequest fileRequest = serverRequest.body(FileRequest.class);
        identityService.removeFiles(identityId, fileRequest.getFileIds());
        return ServerResponse.ok().build();
    }

    public ServerResponse changePassword(ServerRequest serverRequest) {
        IdentityRequest identityRequest = (IdentityRequest) serverRequest.attribute("identityRequest").orElseThrow();
        identityService.changePassword(identityRequest);
        return ServerResponse.ok().build();
    }

    public ServerResponse changeStatus(ServerRequest serverRequest) throws ServletException, IOException {
        IdentityRequest identityRequest = serverRequest.body(IdentityRequest.class);
        identityService.changeStatus(identityRequest);
        return ServerResponse.ok().build();
    }

    public ServerResponse addCourse(ServerRequest serverRequest) {
        UUID identityId = UUID.fromString(serverRequest.param("identityId").orElseThrow());
        UUID courseId = UUID.fromString(serverRequest.param("courseId").orElseThrow());
        identityService.addCourse(identityId, courseId);
        return ServerResponse.ok().build();
    }

    public ServerResponse removeCourse(ServerRequest serverRequest) {
        UUID identityId = UUID.fromString(serverRequest.param("identityId").orElseThrow());
        UUID courseId = UUID.fromString(serverRequest.param("courseId").orElseThrow());
        identityService.removeCourse(identityId, courseId);
        return ServerResponse.ok().build();
    }
}
