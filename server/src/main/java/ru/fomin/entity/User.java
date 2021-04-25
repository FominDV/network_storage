package ru.fomin.entity;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "user_profile")
@Setter
@Getter
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Directory> dataList;

    @OneToOne
    @JoinTable(name = "root_directory",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "directory_id"))
    private Directory rootDirectory;

    @NotNull
    private String login;

    @NotNull
    private String password;

    @NotNull
    private String salt;

    public User(String login, String password, Directory rootDirectory, String salt) {
        this.login = login;
        this.password = password;
        this.rootDirectory = rootDirectory;
        this.salt = salt;
    }

}
