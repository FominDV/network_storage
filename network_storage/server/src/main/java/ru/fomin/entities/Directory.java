package ru.fomin.entities;







import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "directories")
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

    public Directory() {
    }

    public Directory(User user, Directory parentDirectory, String path) {
        this.user = user;
        this.parentDirectory = parentDirectory;
        this.path = path;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FileData> getFiles() {
        return files;
    }

    public void setFiles(List<FileData> files) {
        this.files = files;
    }

    public List<Directory> getNestedDirectories() {
        return nestedDirectories;
    }

    public void setNestedDirectories(List<Directory> nestedDirectories) {
        this.nestedDirectories = nestedDirectories;
    }

    public Long getId() {
        return id;
    }

    public Directory getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }
}
