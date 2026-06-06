package org.burgas.trainingservice.filter;

import jakarta.servlet.http.HttpSession;
import org.burgas.trainingservice.dao.identity.IdentityDetails;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class IdentityFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final List<String> paramUrls = new ArrayList<>(List.of(
            "/api/v1/identities/by-id", "/api/v1/identities/delete",
            "/api/v1/identities/upload-image", "/api/v1/identities/remove-image",
            "/api/v1/identities/upload-files", "/api/v1/identities/remove-files",
            "/api/v1/identities/add-course", "/api/v1/identities/remove-course"
    ));

    private final List<String> bodyUrls = new ArrayList<>(List.of(
            "/api/v1/identities/update", "/api/v1/identities/change-password"
    ));

    @Override
    public @NonNull ServerResponse filter(@NonNull ServerRequest request, @NonNull HandlerFunction<ServerResponse> next) throws Exception {
        if (paramUrls.contains(request.path())) {
            HttpSession session = request.session();
            SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
            Authentication authentication = context.getAuthentication();

            assert authentication != null;
            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                UUID identityId = UUID.fromString(request.param("identityId").orElseThrow());

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(identityId)) {
                    return next.handle(request);

                } else {
                    throw new IllegalArgumentException("Not authorized");
                }

            } else {
                throw new IllegalArgumentException("Not authenticated");
            }

        } else if (bodyUrls.contains(request.path())) {
            HttpSession session = request.session();
            SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
            Authentication authentication = context.getAuthentication();

            assert authentication != null;
            if (authentication.isAuthenticated()) {
                IdentityDetails identityDetails = (IdentityDetails) authentication.getPrincipal();
                IdentityRequest identityRequest = request.body(IdentityRequest.class);

                assert identityDetails != null;
                if (identityDetails.identity().getId().equals(identityRequest.getId())) {
                    request.attributes().put("identityRequest", identityRequest);
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
