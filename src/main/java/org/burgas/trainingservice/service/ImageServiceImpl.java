package org.burgas.trainingservice.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.trainingservice.dao.image.Image;
import org.burgas.trainingservice.repository.ImageRepository;
import org.burgas.trainingservice.service.contract.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class ImageServiceImpl implements ImageService<UUID, Image> {

    private final ImageRepository imageRepository;

    @Override
    public Image findEntity(UUID id) {
        return imageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Image not found"));
    }

    @Override
    @SneakyThrows
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public Image upload(Part part) {
        Image image = Image.builder()
                .name(part.getSubmittedFileName())
                .contentType(part.getContentType())
                .size(part.getSize())
                .preview(true)
                .data(part.getInputStream().readAllBytes())
                .build();
        return imageRepository.save(image);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void remove(UUID id) {
        Image image = findEntity(id);
        imageRepository.delete(image);
    }
}
