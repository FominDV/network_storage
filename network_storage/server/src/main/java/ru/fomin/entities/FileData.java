package ru.fomin.entities;

import javax.persistence.*;

@Entity
@Table(name = "files")
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "directory_id")
    private Directory directory;

    private String name;

    public FileData(Directory directory, String name) {
        this.directory = directory;
        this.name = name;
    }

    public FileData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public Long getId() {
        return id;
    }
}
