package org.burgas.trainingservice.dao.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.file.File;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project", schema = "public")
@NamedEntityGraph(
        name = "project-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "course", subgraph = "course-subgraph"),
                @NamedAttributeNode(value = "task")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "course-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "identities", subgraph = "identities-subgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "identities-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "image"),
                                @NamedAttributeNode(value = "files")
                        }
                )
        }
)
public class Project {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private File task;
}
