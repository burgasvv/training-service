package org.burgas.trainingservice.handler;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.image.Image;
import org.burgas.trainingservice.service.ImageServiceImpl;
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
public class ImageHandler {

    private final ImageServiceImpl imageService;

    public ServerResponse getImageById(ServerRequest serverRequest) {
        UUID imageId = UUID.fromString(serverRequest.param("imageId").orElseThrow());
        Image image = imageService.findEntity(imageId);
        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
    }
}
