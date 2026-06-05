package org.burgas.trainingservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.dao.identity.IdentityDetails;
import org.burgas.trainingservice.mapper.IdentityMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentityDetailsService implements UserDetailsService {

    private final IdentityMapper identityMapper;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return new IdentityDetails(
                identityMapper.identityRepository.findIdentityByEmail(username)
                        .orElseThrow(() -> new IllegalArgumentException("Identity not found in details"))
        );
    }
}
