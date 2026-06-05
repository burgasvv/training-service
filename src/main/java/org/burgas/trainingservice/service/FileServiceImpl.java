package org.burgas.trainingservice.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.repository.FileRepository;
import org.burgas.trainingservice.service.contract.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class FileServiceImpl implements FileService<UUID, File> {

    public final FileRepository fileRepository;

    @Override
    public File findEntity(UUID id) {
        return fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

    @Override
    @SneakyThrows
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public File upload(Part part) {
        File file = File.builder()
                .name(part.getSubmittedFileName())
                .contentType(part.getContentType())
                .size(part.getSize())
                .data(part.getInputStream().readAllBytes())
                .build();
        return fileRepository.save(file);
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void remove(File file) {
        fileRepository.delete(file);
    }
}
