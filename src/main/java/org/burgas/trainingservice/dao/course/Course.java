package org.burgas.trainingservice.dao.course;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.trainingservice.dao.Dao;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dao.project.Project;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course", schema = "public")
@NamedEntityGraph(
        name = "course-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "identities", subgraph = "identities-subgraph"),
                @NamedAttributeNode(value = "projects", subgraph = "projects-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "identities-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "image"),
                                @NamedAttributeNode(value = "files")
                        }
                ),
                @NamedSubgraph(
                        name = "projects-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "task")
                        }
                )
        }
)
public class Course implements Dao {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "courses")
    private Set<Identity> identities = new LinkedHashSet<>();

    public void addIdentity(Identity identity) {
        this.identities.add(identity);
        identity.getCourses().add(this);
    }

    public void removeIdentity(Identity identity) {
        this.identities.remove(identity);
        identity.getCourses().remove(this);
    }

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Set<Project> projects = new LinkedHashSet<>();
}
