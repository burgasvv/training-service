package org.burgas.trainingservice.dao.identity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.burgas.trainingservice.dao.Dao;
import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.dao.image.Image;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "identity", schema = "public")
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "identity_file",
            joinColumns = {
                    @JoinColumn(name = "identity_id", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "file_id", referencedColumnName = "id")
            }
    )
    private Set<File> files = new HashSet<>();
}
