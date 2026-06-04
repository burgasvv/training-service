package org.burgas.trainingservice.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dao.image.Image;
import org.burgas.trainingservice.dto.Dependency;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityDependency implements Dependency {

    private UUID id;
    private String email;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String about;
    private Image image;
}
