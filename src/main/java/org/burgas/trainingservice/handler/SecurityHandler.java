package org.burgas.trainingservice.handler;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class SecurityHandler {

    public ServerResponse getCsrfToken(ServerRequest serverRequest) {
        CsrfToken csrfToken = (CsrfToken) serverRequest.attribute("_csrf").orElseThrow();
        return ServerResponse.ok().body(csrfToken);
    }

    public ServerResponse login(ServerRequest ignoredServerRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        if (authentication.isAuthenticated()) {
            return ServerResponse.ok().body("You successfully logged in");
        } else {
            throw new IllegalArgumentException("Not authenticated");
        }
    }

    public ServerResponse logout(ServerRequest serverRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.clearContext();
            HttpSession session = serverRequest.session();
            session.removeAttribute("SPRING_SECURITY_CONTEXT");
            return ServerResponse.ok().body("You successfully logged out");
        } else {
            throw new IllegalArgumentException("You are not authenticated for logout");
        }
    }
}
