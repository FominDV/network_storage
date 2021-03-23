package ru.fomin.services.db_services;

import ru.fomin.dao.UserDao;
import ru.fomin.entities.Directory;
import ru.fomin.entities.User;

public class UserService {

    /**
     * Helper class for requests to User entity from database.
     */
    private final static UserDao USER_DAO = new UserDao();

    /**
     * Verifies login and password for authorizing.
     */
    public boolean isValidUserData(String login, String password) {
        User user = USER_DAO.getUsersByLogin(login);
        return user != null && user.getPassword().equals(password);
    }

    /**
     * Creates new user.
     *
     * @return - false if user with this login already exists
     */
    public boolean createUser(String login, String password, String root) {
        if (USER_DAO.getUsersByLogin(login) == null) {
            Directory dataRoot = new Directory();
            User user = new User(login, password, dataRoot);
            dataRoot.setUser(user);
            dataRoot.setPath(root);
            USER_DAO.create(user, dataRoot);
            return true;
        }
        return false;
    }

    public User getUserByLogin(String login) {
        return USER_DAO.getUsersByLogin(login);
    }

    public Directory getRootDirectoryByLogin(String login) {
        User user = getUserByLogin(login);
        return user.getRootDirectory();
    }
}
