package ru.fomin.entities;







import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "directories")
@NoArgsConstructor
public class Directory {

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
