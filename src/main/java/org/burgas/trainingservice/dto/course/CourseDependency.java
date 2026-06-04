package org.burgas.trainingservice.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dto.Dependency;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDependency implements Dependency {

    private UUID id;
    private String name;
    private String description;
}
