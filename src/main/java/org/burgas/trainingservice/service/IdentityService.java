package org.burgas.trainingservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dto.course.CourseResponse;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.burgas.trainingservice.dto.identity.IdentityResponse;
import org.burgas.trainingservice.mapper.IdentityMapper;
import org.burgas.trainingservice.redis.CacheHandler;
import org.burgas.trainingservice.redis.RedisKeys;
import org.burgas.trainingservice.service.contract.CollectService;
import org.burgas.trainingservice.service.contract.DesignService;
import org.burgas.trainingservice.service.contract.FindService;
import org.burgas.trainingservice.service.contract.ModifyService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        identityMapper.identityRepository.delete(identity);
        handleCache(identity);
    }
}
