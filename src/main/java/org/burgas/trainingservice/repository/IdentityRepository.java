package org.burgas.trainingservice.repository;

import org.burgas.trainingservice.dao.identity.Identity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, UUID> {

    @Override
    @EntityGraph(value = "identity-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull Optional<Identity> findById(@NonNull UUID uuid);

    @Override
    @EntityGraph(value = "identity-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull List<Identity> findAll();

    Optional<Identity> findIdentityByEmail(String email);
}
