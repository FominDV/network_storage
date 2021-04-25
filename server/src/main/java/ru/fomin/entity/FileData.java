package ru.fomin.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor
public class FileData implements Serializable {

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
