package org.burgas.trainingservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest implements Request {

    private UUID id;
    private String name;
    private String description;
    private UUID courseId;
}
