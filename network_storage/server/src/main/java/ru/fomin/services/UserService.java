package ru.fomin.services;

import ru.fomin.dao.UserDao;
import ru.fomin.entities.Directory;
import ru.fomin.entities.User;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UserService {

    private final static UserDao USER_DAO = new UserDao();

    public boolean isValidUserData(String login, String password) {
        User user = USER_DAO.getUsersByLogin(login);
        return user != null && user.getPassword().equals(password);
    }

    public boolean createUser(String login, String password, String root) {
        if (USER_DAO.getUsersByLogin(login) == null) {
            Directory dataRoot = new Directory();
            User user=new User(login,password,dataRoot);
            dataRoot.setUser(user);
            dataRoot.setPath(root);
            USER_DAO.create(user, dataRoot);
            return true;
        }
        return false;
    }

    public User getUserByLogin(String login){
       return USER_DAO.getUsersByLogin(login);
    }

    public Path getRootDirectoryPathByLogin(String login){
        User user = getUserByLogin(login);
        return Paths.get(user.getRootDirectory().getPath());
    }
}
