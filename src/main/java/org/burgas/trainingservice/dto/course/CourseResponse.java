package org.burgas.trainingservice.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dto.Response;
import org.burgas.trainingservice.dto.identity.IdentityDependency;
import org.burgas.trainingservice.dto.project.ProjectDependency;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse implements Response {

    private UUID id;
    private String name;
    private String description;
    private Set<IdentityDependency> identities;
    private Set<ProjectDependency> projects;
}
