package org.burgas.trainingservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dao.project.Project;
import org.burgas.trainingservice.dto.course.CourseRequest;
import org.burgas.trainingservice.dto.course.CourseResponse;
import org.burgas.trainingservice.dto.identity.IdentityResponse;
import org.burgas.trainingservice.dto.project.ProjectResponse;
import org.burgas.trainingservice.mapper.CourseMapper;
import org.burgas.trainingservice.redis.CacheHandler;
import org.burgas.trainingservice.redis.RedisKeys;
import org.burgas.trainingservice.service.contract.CollectService;
import org.burgas.trainingservice.service.contract.DesignService;
import org.burgas.trainingservice.service.contract.FindService;
import org.burgas.trainingservice.service.contract.ModifyService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class CourseService implements CacheHandler<Course>, CollectService<CourseResponse>,
        FindService<UUID, Course, CourseResponse>, DesignService<UUID, CourseRequest, CourseResponse>,
        ModifyService<CourseRequest, CourseResponse> {

    private final CourseMapper courseMapper;

    private final RedisTemplate<String, CourseResponse> courseRedisTemplate;
    private final RedisTemplate<String, IdentityResponse> identityRedisTemplate;
    private final RedisTemplate<String, ProjectResponse> projectRedisTemplate;

    @Override
    public void handleCache(Course entity) {
        String courseKey = String.format(RedisKeys.courseKey, entity.getId());
        if (courseRedisTemplate.hasKey(courseKey)) courseRedisTemplate.delete(courseKey);

        Set<Identity> identities = entity.getIdentities();
        if (!identities.isEmpty()) {
            identities.forEach(identity -> {
                String identityKey = String.format(RedisKeys.identityKey, identity.getId());
                if (identityRedisTemplate.hasKey(identityKey)) identityRedisTemplate.delete(identityKey);
            });
        }

        Set<Project> projects = entity.getProjects();
        if (!projects.isEmpty()) {
            projects.forEach(project -> {
                String projectKey = String.format(RedisKeys.projectKey, project.getId());
                if (projectRedisTemplate.hasKey(projectKey)) projectRedisTemplate.delete(projectKey);
            });
        }
    }

    @Override
    public Course findEntity(UUID uuid) {
        return courseMapper.courseRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    @Override
    public CourseResponse findById(UUID uuid) {
        return null;
    }

    @Override
    public Set<CourseResponse> findAll() {
        return courseMapper.courseRepository.findAll()
                .parallelStream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toSet());
    }

    @Override
    public CourseResponse create(CourseRequest request) {
        return null;
    }

    @Override
    public CourseResponse update(CourseRequest request) {
        return null;
    }

    @Override
    public void delete(UUID uuid) {

    }
}
