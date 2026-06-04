package org.burgas.trainingservice.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.dao.image.Image;
import org.burgas.trainingservice.dto.Response;
import org.burgas.trainingservice.dto.course.CourseDependency;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityResponse implements Response {

    private UUID id;
    private String email;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String about;
    private Image image;
    private Set<File> files;
    private Set<CourseDependency> courses;
}
