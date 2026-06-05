package org.burgas.trainingservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.identity.Authority;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dto.identity.IdentityDependency;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.burgas.trainingservice.dto.identity.IdentityResponse;
import org.burgas.trainingservice.mapper.contract.Mapper;
import org.burgas.trainingservice.repository.IdentityRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IdentityMapper implements Mapper<IdentityRequest, Identity, IdentityDependency, IdentityResponse> {

    public final IdentityRepository identityRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectFactory<CourseMapper> courseMapperObjectFactory;

    private CourseMapper getCourseMapper() {
        return courseMapperObjectFactory.getObject();
    }

    @Override
    public Identity toEntity(IdentityRequest request) {
        return identityRepository.findById(handleData(request.getId(), new UUID(0, 0)))
                .map(identity -> Identity.builder()
                        .id(identity.getId())
                        .authority(handleData(request.getAuthority(), identity.getAuthority()))
                        .email(handleData(request.getEmail(), identity.getEmail()))
                        .password(identity.getPassword())
                        .status(identity.getStatus())
                        .firstname(handleData(request.getFirstname(), identity.getFirstname()))
                        .lastname(handleData(request.getLastname(), identity.getLastname()))
                        .patronymic(handleData(request.getPatronymic(), identity.getPatronymic()))
                        .about(handleData(request.getAbout(), identity.getAbout()))
                        .image(identity.getImage())
                        .files(identity.getFiles())
                        .courses(identity.getCourses())
                        .build())
                .orElseGet(() -> {
                    String newPassword = handleDataException(request.getPassword(), "Password is null");
                    return Identity.builder()
                            .authority(handleData(request.getAuthority(), Authority.USER))
                            .email(handleDataException(request.getEmail(), "Email is null"))
                            .password(passwordEncoder.encode(newPassword))
                            .status(handleData(request.getStatus(), true))
                            .firstname(handleDataException(request.getFirstname(), "First name is null"))
                            .lastname(handleDataException(request.getLastname(), "Last name is null"))
                            .patronymic(handleDataException(request.getPatronymic(), "Patronymic is null"))
                            .about(handleDataException(request.getAbout(), "About is null"))
                            .image(null)
                            .files(new LinkedHashSet<>())
                            .courses(new LinkedHashSet<>())
                            .build();
                });
    }

    @Override
    public IdentityDependency toDependency(Identity entity) {
        return IdentityDependency.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .patronymic(entity.getPatronymic())
                .about(entity.getAbout())
                .image(entity.getImage())
                .build();
    }

    @Override
    public IdentityResponse toResponse(Identity entity) {
        return IdentityResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .patronymic(entity.getPatronymic())
                .about(entity.getAbout())
                .image(entity.getImage())
                .files(entity.getFiles())
                .courses(
                        entity.getCourses().parallelStream()
                                .map(course -> getCourseMapper().toDependency(course))
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
