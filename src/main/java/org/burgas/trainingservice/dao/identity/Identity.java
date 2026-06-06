package org.burgas.trainingservice.dao.identity;

import jakarta.persistence.*;
import lombok.*;
import org.burgas.trainingservice.dao.Dao;
import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.dao.image.Image;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "identity", schema = "public")
@NamedEntityGraph(
        name = "identity-entity-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "image"),
                @NamedAttributeNode(value = "files"),
                @NamedAttributeNode(value = "courses", subgraph = "courses-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "courses-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "projects", subgraph = "projects-subgraph")
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
public class Identity implements Dao {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "authority")
    @Enumerated(value = EnumType.STRING)
    private Authority authority;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "about")
    private String about;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_file",
            joinColumns = {
                    @JoinColumn(name = "identity_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "file_id", referencedColumnName = "id")
            }
    )
    private Set<File> files = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_course",
            joinColumns = {
                    @JoinColumn(name = "identity_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "course_id", referencedColumnName = "id")
            }
    )
    private Set<Course> courses = new LinkedHashSet<>();

    public void addCourse(Course course) {
        this.courses.add(course);
        course.getIdentities().add(this);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getIdentities().remove(this);
    }
}
