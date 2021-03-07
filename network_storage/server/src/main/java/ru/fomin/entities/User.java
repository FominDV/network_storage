package ru.fomin.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Directory> dataList;

    @OneToOne
    @JoinTable(name = "root_directory",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "directory_id"))
    private Directory rootDirectory;

    private String login;
    private String password;

    public User(String login, String password, Directory rootDirectory) {
        this.login = login;
        this.password = password;
        this.rootDirectory = rootDirectory;
    }

    public User() {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Directory> getDataList() {
        return dataList;
    }

    public void setDataList(List<Directory> dataList) {
        this.dataList = dataList;
    }

    public Directory getRootDirectory() {
        return rootDirectory;
    }
}
