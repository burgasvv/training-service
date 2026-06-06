package org.burgas.trainingservice.security;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.service.IdentityDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final IdentityDetailsService identityDetailsService;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(identityDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        return httpSecurity
                .csrf(csrf -> csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()))
                .cors(cors -> cors.configurationSource(new UrlBasedCorsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic
                        .securityContextRepository(new HttpSessionSecurityContextRepository())
                )
                .authenticationManager(authenticationManager())
                .authorizeHttpRequests(authorization -> authorization

                        .requestMatchers(
                                "/api/v1/security/csrf-token",

                                "/api/v1/identities/create",

                                "/api/v1/images/by-id", "/api/v1/files/by-id"
                        )
                        .permitAll()

                        .requestMatchers(
                                "/api/v1/security/login", "/api/v1/security/logout",

                                "/api/v1/identities/by-id", "/api/v1/identities/update", "/api/v1/identities/delete",
                                "/api/v1/identities/upload-image", "/api/v1/identities/remove-image",
                                "/api/v1/identities/upload-files", "/api/v1/identities/remove-files",
                                "/api/v1/identities/change-password", "/api/v1/identities/add-course",
                                "/api/v1/identities/remove-course",

                                "/api/v1/courses/by-id", "/api/v1/courses"
                        )
                        .hasAnyAuthority("ADMIN", "USER")

                        .requestMatchers(
                                "/api/v1/identities", "/api/v1/identities/change-status",

                                "/api/v1/courses/create", "/api/v1/courses/update", "/api/v1/courses/delete"
                        )
                        .hasAnyAuthority("ADMIN")
                )
                .build();
    }
}
