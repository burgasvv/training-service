package org.burgas.trainingservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dto.course.CourseDependency;
import org.burgas.trainingservice.dto.course.CourseRequest;
import org.burgas.trainingservice.dto.course.CourseResponse;
import org.burgas.trainingservice.repository.CourseRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CourseMapper implements Mapper<CourseRequest, Course, CourseDependency, CourseResponse> {

    public final CourseRepository courseRepository;
    private final ObjectFactory<IdentityMapper> identityMapperObjectFactory;
    private final ObjectFactory<ProjectMapper> projectMapperObjectFactory;

    private IdentityMapper getIdentityMapper() {
        return identityMapperObjectFactory.getObject();
    }

    private ProjectMapper getProjectMapper() {
        return projectMapperObjectFactory.getObject();
    }

    @Override
    public Course toEntity(CourseRequest request) {
        return courseRepository.findById(handleData(request.getId(), new UUID(0,0)))
                .map(course -> Course.builder()
                        .id(course.getId())
                        .name(handleData(request.getName(), course.getName()))
                        .description(handleData(request.getDescription(), course.getDescription()))
                        .identities(course.getIdentities())
                        .projects(course.getProjects())
                        .build()
                )
                .orElseGet(() -> Course.builder()
                        .name(handleDataException(request.getName(), "Name is null"))
                        .description(handleDataException(request.getDescription(), "Description is null"))
                        .identities(new LinkedHashSet<>())
                        .projects(new LinkedHashSet<>())
                        .build()
                );
    }

    @Override
    public CourseDependency toDependency(Course entity) {
        return CourseDependency.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    @Override
    public CourseResponse toResponse(Course entity) {
        return CourseResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .identities(
                        entity.getIdentities().parallelStream()
                                .map(identity -> getIdentityMapper().toDependency(identity))
                                .collect(Collectors.toSet())
                )
                .projects(
                        entity.getProjects().parallelStream()
                                .map(project -> getProjectMapper().toDependency(project))
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
