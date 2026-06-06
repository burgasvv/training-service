package org.burgas.trainingservice.filter;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dao.identity.IdentityDetails;
import org.burgas.trainingservice.dao.project.Project;
import org.burgas.trainingservice.service.ProjectService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final ProjectService projectService;

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next) throws Exception {
        if (request.path().equals("/api/v1/projects/by-id")) {
            HttpSession session = request.session();
            SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
            Authentication authentication = securityContext.getAuthentication();

            assert authentication != null;
            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                UUID projectId = UUID.fromString(request.param("projectId").orElseThrow());

                Project project = projectService.findEntity(projectId);
                Set<UUID> courseIdentityIds = project.getCourse().getIdentities()
                        .parallelStream().map(Identity::getId).collect(Collectors.toSet());

                assert identityDetails != null;
                if (
                        (courseIdentityIds.contains(identityDetails.identity().getId()) &&
                         identityDetails.identity().getAuthority().name().equals("ADMIN")) ||
                        (courseIdentityIds.contains(identityDetails.identity().getId()) ||
                         identityDetails.identity().getAuthority().name().equals("ADMIN"))
                ) {
                    return next.handle(request);

                } else {
                    throw new IllegalArgumentException("Not authorized");
                }

            } else {
                throw new IllegalArgumentException("Not authenticated");
            }

        } else {
            return next.handle(request);
        }
    }
}
