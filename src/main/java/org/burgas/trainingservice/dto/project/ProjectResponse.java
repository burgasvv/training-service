package org.burgas.trainingservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.dto.Response;
import org.burgas.trainingservice.dto.course.CourseDependency;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse implements Response {

    private UUID id;
    private String name;
    private String description;
    private CourseDependency course;
    private File task;
}
