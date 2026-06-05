package org.burgas.trainingservice.dao.identity;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record IdentityDetails(Identity identity) implements UserDetails {

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(identity.getAuthority());
    }

    @Override
    public @Nullable String getPassword() {
        return identity.getPassword();
    }

    @Override
    public @NonNull String getUsername() {
        return identity.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return identity.getStatus() || !UserDetails.super.isEnabled();
    }
}