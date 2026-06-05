package org.burgas.trainingservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.project.Project;
import org.burgas.trainingservice.dto.project.ProjectDependency;
import org.burgas.trainingservice.dto.project.ProjectRequest;
import org.burgas.trainingservice.dto.project.ProjectResponse;
import org.burgas.trainingservice.mapper.contract.Mapper;
import org.burgas.trainingservice.repository.ProjectRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectMapper implements Mapper<ProjectRequest, Project, ProjectDependency, ProjectResponse> {

    public final ProjectRepository projectRepository;
    private final ObjectFactory<CourseMapper> courseMapperObjectFactory;

    private CourseMapper getCourseMapper() {
        return courseMapperObjectFactory.getObject();
    }

    @Override
    public Project toEntity(ProjectRequest request) {
        return projectRepository.findById(handleData(request.getId(), new UUID(0, 0)))
                .map(project -> {
                    Course course = getCourseMapper().courseRepository
                            .findById(handleData(request.getCourseId(), new UUID(0, 0)))
                            .orElse(null);
                    return Project.builder()
                            .id(project.getId())
                            .name(handleData(request.getName(), project.getName()))
                            .description(handleData(request.getDescription(), project.getDescription()))
                            .course(handleData(course, project.getCourse()))
                            .task(project.getTask())
                            .build();
                })
                .orElseGet(() -> {
                    Course course = getCourseMapper().courseRepository
                            .findById(handleData(request.getCourseId(), new UUID(0, 0)))
                            .orElse(null);
                    return Project.builder()
                            .name(handleDataException(request.getName(), "Name is null"))
                            .description(handleDataException(request.getDescription(), "Description is null"))
                            .course(handleDataException(course, "Course is null"))
                            .task(null)
                            .build();
                });
    }

    @Override
    public ProjectDependency toDependency(Project entity) {
        return ProjectDependency.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    @Override
    public ProjectResponse toResponse(Project entity) {
        return ProjectResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .course(
                        Optional.ofNullable(entity.getCourse())
                                .map(course -> getCourseMapper().toDependency(course))
                                .orElse(null)
                )
                .task(entity.getTask())
                .build();
    }
}
