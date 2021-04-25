package ru.fomin.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "directory_id")
    private Directory directory;

    private String name;

    public FileData(Directory directory, String name) {
        this.directory = directory;
        this.name = name;
    }

}