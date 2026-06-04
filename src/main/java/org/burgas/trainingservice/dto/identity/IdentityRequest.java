package org.burgas.trainingservice.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dao.identity.Authority;
import org.burgas.trainingservice.dto.Request;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityRequest implements Request {

    private UUID id;
    private Authority authority;
    private String email;
    private String password;
    private Boolean status;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String about;
}
