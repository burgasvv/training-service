package org.burgas.trainingservice.service;

import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dao.image.Image;
import org.burgas.trainingservice.dto.course.CourseResponse;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.burgas.trainingservice.dto.identity.IdentityResponse;
import org.burgas.trainingservice.mapper.IdentityMapper;
import org.burgas.trainingservice.redis.CacheHandler;
import org.burgas.trainingservice.redis.RedisKeys;
import org.burgas.trainingservice.repository.FileRepository;
import org.burgas.trainingservice.service.contract.CollectService;
import org.burgas.trainingservice.service.contract.DesignService;
import org.burgas.trainingservice.service.contract.FindService;
import org.burgas.trainingservice.service.contract.ModifyService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class IdentityService implements CacheHandler<Identity>,  FindService<UUID, Identity, IdentityResponse>,
        CollectService<IdentityResponse>, DesignService<UUID, IdentityRequest, IdentityResponse>,
        ModifyService<IdentityRequest, IdentityResponse> {

    private final IdentityMapper identityMapper;
    private final ImageServiceImpl imageService;
    private final FileServiceImpl fileService;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseService courseService;

    private final RedisTemplate<String, IdentityResponse> identityRedisTemplate;
    private final RedisTemplate<String, CourseResponse> courseRedisTemplate;

    @Override
    public void handleCache(Identity entity) {
        String identityKey = String.format(RedisKeys.identityKey, entity.getId());
        if (identityRedisTemplate.hasKey(identityKey)) identityRedisTemplate.delete(identityKey);

        Set<Course> courses = entity.getCourses();
        if (!courses.isEmpty()) {
            courses.forEach(course -> {
                String courseKey = String.format(RedisKeys.courseKey, course.getId());
                if (courseRedisTemplate.hasKey(courseKey)) courseRedisTemplate.delete(courseKey);
            });
        }
    }

    @Override
    public Identity findEntity(UUID uuid) {
        return identityMapper.identityRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Identity not found"));
    }

    @Override
    public IdentityResponse findById(UUID uuid) {
        String identityKey = String.format(RedisKeys.identityKey, uuid);
        if (identityRedisTemplate.hasKey(identityKey)) {
            return identityRedisTemplate.opsForValue().get(identityKey);
        } else {
            IdentityResponse identityResponse = identityMapper.toResponse(findEntity(uuid));
            identityRedisTemplate.opsForValue().set(identityKey, identityResponse);
            return identityResponse;
        }
    }

    @Override
    public Set<IdentityResponse> findAll() {
        return identityMapper.identityRepository.findAll()
                .parallelStream()
                .map(identityMapper::toResponse)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public IdentityResponse create(IdentityRequest request) {
        Identity identity = identityMapper.identityRepository.save(identityMapper.toEntity(request));
        IdentityResponse identityResponse = identityMapper.toResponse(identity);
        String identityKey = String.format(RedisKeys.identityKey, identityResponse.getId());
        identityRedisTemplate.opsForValue().set(identityKey, identityResponse);
        return identityResponse;
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public IdentityResponse update(IdentityRequest request) {
        if (request.getId() == null) throw new IllegalArgumentException("Request id is null");
        Identity identity = identityMapper.identityRepository.save(identityMapper.toEntity(request));
        handleCache(identity);
        IdentityResponse identityResponse = identityMapper.toResponse(identity);
        String identityKey = String.format(RedisKeys.identityKey, identityResponse.getId());
        identityRedisTemplate.opsForValue().set(identityKey, identityResponse);
        return identityResponse;
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void delete(UUID uuid) {
        Identity identity = findEntity(uuid);
        identity.getFiles().forEach(fileService::remove);
        identityMapper.identityRepository.delete(identity);
        handleCache(identity);
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void uploadImage(UUID identityId, Part part) {
        if (part.getContentType().startsWith("image")) {
            Identity identity = findEntity(identityId);
            Image image = imageService.upload(part);
            identity.setImage(image);
            handleCache(identity);
        } else {
            throw new IllegalArgumentException("Wrong part content type! Must be image");
        }
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void removeImage(UUID identityId) {
        Identity identity = findEntity(identityId);
        Image image = identity.getImage();
        if (image != null) {
            identity.setImage(null);
            imageService.remove(image);
            handleCache(identity);
        } else {
            throw new IllegalArgumentException("Identity image is null");
        }
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void uploadFiles(UUID identityId, List<Part> parts) {
        Identity identity = findEntity(identityId);
        parts.forEach(part -> {
            if (part.getContentType().startsWith("application")) {
                File file = fileService.upload(part);
                identity.getFiles().add(file);
            } else {
                throw new IllegalArgumentException("Wrong part content type! Must be application");
            }
        });
        handleCache(identity);
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void removeFiles(UUID identityId, Set<UUID> fileIds) {
        Identity identity = findEntity(identityId);
        Set<UUID> identityFileIds = identity.getFiles().parallelStream().map(File::getId).collect(Collectors.toSet());
        if (identityFileIds.containsAll(fileIds)) {
            List<File> files = fileRepository.findAllById(fileIds);
            files.forEach(identity.getFiles()::remove);
            fileRepository.deleteAllById(fileIds);
            handleCache(identity);
        } else {
            throw new IllegalArgumentException("Identity files not contains input file ids");
        }
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void changePassword(IdentityRequest identityRequest) {
        if (identityRequest.getId() == null) throw new IllegalArgumentException("Request identity id is null");
        if (identityRequest.getPassword() == null || identityRequest.getPassword().isEmpty())
            throw new IllegalArgumentException("Identity password is null");
        Identity identity = findEntity(identityRequest.getId());
        if (!passwordEncoder.matches(identityRequest.getPassword(), identity.getPassword())) {
            identity.setPassword(passwordEncoder.encode(identityRequest.getPassword()));
            handleCache(identity);
        } else {
            throw new IllegalArgumentException("Passwords matched");
        }
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void changeStatus(IdentityRequest identityRequest) {
        if (identityRequest.getId() == null) throw new IllegalArgumentException("Request identity id is null");
        if (identityRequest.getStatus() == null) throw new IllegalArgumentException("Request identity status is null");
        Identity identity = findEntity(identityRequest.getId());
        if (!identity.getStatus().equals(identityRequest.getStatus())) {
            identity.setStatus(identityRequest.getStatus());
            handleCache(identity);
        } else {
            throw new IllegalArgumentException("Statuses matched");
        }
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void addCourse(UUID identityId, UUID courseId) {
        Identity identity = findEntity(identityId);
        Course course = courseService.findEntity(courseId);
        Set<UUID> identityCourseIds = identity.getCourses().parallelStream().map(Course::getId).collect(Collectors.toSet());
        if (!identityCourseIds.contains(course.getId())) {
            identity.addCourse(course);
            handleCache(identity);
        } else {
            throw new IllegalArgumentException("Identity already subscribed on course");
        }
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void removeCourse(UUID identityId, UUID courseId) {
        Identity identity = findEntity(identityId);
        Course course = courseService.findEntity(courseId);
        Set<UUID> identityCourseIds = identity.getCourses().parallelStream().map(Course::getId).collect(Collectors.toSet());
        if (identityCourseIds.contains(course.getId())) {
            identity.removeCourse(course);
            handleCache(identity);
        } else {
            throw new IllegalArgumentException("Identity not subscribed on course for remove");
        }
    }
}
