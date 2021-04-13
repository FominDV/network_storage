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
     *
     * @return - [0] - boolean, true if login and password is valid, [1] - Long, id of authorized client
     */
    public Object[] isValidUserData(String login, String password) {
        User user = USER_DAO.getUsersByLogin(login);
        Object[] response = new Object[2];
        if (user != null) {
            response[0] = user.getPassword().equals(password);
            response[1] = user.getId();
        }else {
            response[0] = false;
        }
        return response;
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

    /**
     * Changing password.
     *
     * @param currentPassword - current password of user
     * @param newPassword     - new password
     * @param id              - id of user
     * @return - true if password was changed
     */
    public boolean changePassword(String currentPassword, String newPassword, Long id) {
        User user = USER_DAO.findUserById(id);
        if (user == null || !user.getPassword().equals(currentPassword)) {
            return false;
        }
        user.setPassword(newPassword);
        USER_DAO.updateUser(user);
        return true;
    }
}
