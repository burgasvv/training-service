package org.burgas.trainingservice.repository;

import org.burgas.trainingservice.dao.project.Project;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Override
    @EntityGraph(value = "project-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull Optional<Project> findById(@NonNull UUID uuid);

    @Override
    @EntityGraph(value = "project-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull List<Project> findAll();
}
