package ru.fomin.service.db;

import ru.fomin.entity.Directory;
import ru.fomin.entity.User;

/**
 * Helper for requests to User entity from database.
 */
public interface UserService {

    /**
     * Verifies login and password for authorizing.
     *
     * @return - [0] - boolean, true if login and password is valid, [1] - Long, id of authorized client
     */
    Object[] isValidUserData(String login, String password);

    /**
     * Creates new user.
     *
     * @return - false if user with this login already exists
     */
    boolean createUser(String login, String password, String root);

    User getUserByLogin(String login);

    Directory getRootDirectoryByLogin(String login);

    /**
     * Changing password.
     *
     * @param currentPassword - current password of user
     * @param newPassword     - new password
     * @param id              - id of user
     * @return - true if password was changed
     */
    boolean changePassword(String currentPassword, String newPassword, Long id);

}
