package org.burgas.trainingservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.project.Project;
import org.burgas.trainingservice.dto.course.CourseResponse;
import org.burgas.trainingservice.dto.project.ProjectRequest;
import org.burgas.trainingservice.dto.project.ProjectResponse;
import org.burgas.trainingservice.mapper.ProjectMapper;
import org.burgas.trainingservice.redis.CacheHandler;
import org.burgas.trainingservice.redis.RedisKeys;
import org.burgas.trainingservice.service.contract.DesignService;
import org.burgas.trainingservice.service.contract.FindService;
import org.burgas.trainingservice.service.contract.ModifyService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
public class ProjectService implements CacheHandler<Project>, FindService<UUID, Project, ProjectResponse>,
        DesignService<UUID, ProjectRequest, ProjectResponse>, ModifyService<ProjectRequest, ProjectResponse> {

    private final ProjectMapper projectMapper;

    private final RedisTemplate<String, ProjectResponse> projectRedisTemplate;
    private final RedisTemplate<String, CourseResponse> courseRedisTemplate;

    @Override
    public void handleCache(Project entity) {
        String projectKey = String.format(RedisKeys.projectKey, entity.getId());
        if (projectRedisTemplate.hasKey(projectKey)) projectRedisTemplate.delete(projectKey);

        Course course = entity.getCourse();
        if (course != null) {
            String courseKey = String.format(RedisKeys.courseKey, course.getId());
            if (courseRedisTemplate.hasKey(courseKey)) courseRedisTemplate.delete(courseKey);
        }
    }

    @Override
    public Project findEntity(UUID uuid) {
        return projectMapper.projectRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    @Override
    public ProjectResponse findById(UUID uuid) {
        String projectKey = String.format(RedisKeys.projectKey, uuid);
        if (projectRedisTemplate.hasKey(projectKey)) {
            return projectRedisTemplate.opsForValue().get(projectKey);
        } else {
            ProjectResponse projectResponse = projectMapper.toResponse(findEntity(uuid));
            projectRedisTemplate.opsForValue().set(projectKey, projectResponse);
            return projectResponse;
        }
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public ProjectResponse create(ProjectRequest request) {
        Project project = projectMapper.projectRepository.save(projectMapper.toEntity(request));
        handleCache(project);
        String projectKey = String.format(RedisKeys.projectKey, project.getId());
        ProjectResponse projectResponse = projectMapper.toResponse(project);
        projectRedisTemplate.opsForValue().set(projectKey, projectResponse);
        return projectResponse;
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public ProjectResponse update(ProjectRequest request) {
        if (request.getId() == null) throw new IllegalArgumentException("Project request id is null");
        Project project = projectMapper.projectRepository.save(projectMapper.toEntity(request));
        handleCache(project);
        String projectKey = String.format(RedisKeys.projectKey, project.getId());
        ProjectResponse projectResponse = projectMapper.toResponse(project);
        projectRedisTemplate.opsForValue().set(projectKey, projectResponse);
        return projectResponse;
    }

    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
            rollbackFor = {Exception.class, Throwable.class, RuntimeException.class}
    )
    public void delete(UUID uuid) {
        Project project = findEntity(uuid);
        projectMapper.projectRepository.delete(project);
        handleCache(project);
    }
}
