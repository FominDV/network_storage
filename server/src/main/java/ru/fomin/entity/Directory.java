package ru.fomin.entity;







import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "directory")
@NoArgsConstructor
public class Directory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileData> files;

    @OneToMany
    @JoinTable(name = "DirToDir",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id"))
    private List<Directory> nestedDirectories;

    @ManyToOne
    @JoinTable(name = "DirToDir",
            joinColumns = @JoinColumn(name = "child_id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id"))
    private Directory parentDirectory;

    private String path;

    public Directory(User user, Directory parentDirectory, String path) {
        this.user = user;
        this.parentDirectory = parentDirectory;
        this.path = path;
    }

}
