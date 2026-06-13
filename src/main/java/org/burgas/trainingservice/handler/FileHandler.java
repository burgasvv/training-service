package org.burgas.trainingservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.service.FileServiceImpl;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileHandler {

    private final FileServiceImpl fileService;

    public ServerResponse getFileById(ServerRequest serverRequest) {
        UUID fileId = UUID.fromString(serverRequest.param("fileId").orElseThrow());
        File file = fileService.findEntity(fileId);
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(new InputStreamResource(new ByteArrayInputStream(file.getData())));
    }
}
