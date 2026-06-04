package org.burgas.trainingservice.repository;

import org.burgas.trainingservice.dao.course.Course;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Override
    @EntityGraph(value = "course-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull Optional<Course> findById(@NonNull UUID uuid);

    @Override
    @EntityGraph(value = "course-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    @NonNull List<Course> findAll();
}
