package ru.fomin.service.db.impl;

import ru.fomin.dao.UserDao;
import ru.fomin.dao.impl.UserDaoImpl;
import ru.fomin.domain.CodePair;
import ru.fomin.entity.Directory;
import ru.fomin.entity.User;
import ru.fomin.factory.EncoderFactory;
import ru.fomin.service.db.UserService;
import ru.fomin.util.encoder.Codable;

/**
 * Helper class for requests to User entity from database.
 */
public class UserServiceImpl implements UserService {

    private final Codable codable = EncoderFactory.getEncoder();

    private final static UserDao USER_DAO = new UserDaoImpl();

    /**
     * Verifies login and password for authorizing.
     *
     * @return - [0] - boolean, true if login and password is valid, [1] - Long, id of authorized client
     */
    @Override
    public Object[] isValidUserData(String login, String password) {

        User user = USER_DAO.getUsersByLogin(login);

        Object[] response = new Object[2];
        if (user != null) {
            String encodedPassword = codable.encode(password, user.getSalt());
            response[0] = user.getPassword().equals(encodedPassword);
            response[1] = user.getId();
        } else {
            response[0] = false;
        }

        if ((Boolean) response[0]) {
            updateUserSalt(password, user);
        }

        return response;
    }

    /**
     * Creates new user.
     *
     * @return - false if user with this login already exists
     */
    @Override
    public boolean createUser(String login, String password, String root) {

        CodePair codePair = codable.encode(password);

        if (USER_DAO.getUsersByLogin(login) == null) {
            Directory dataRoot = new Directory();
            User user = new User(login, codePair.getValue(), dataRoot, codePair.getSalt());
            dataRoot.setUser(user);
            dataRoot.setPath(root);
            USER_DAO.save(user, dataRoot);
            return true;
        }
        return false;
    }

    @Override
    public User getUserByLogin(String login) {
        return USER_DAO.getUsersByLogin(login);
    }

    @Override
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
    @Override
    public boolean changePassword(String currentPassword, String newPassword, Long id) {

        User user = USER_DAO.getById(id, User.class);
        currentPassword = codable.encode(currentPassword, user.getSalt());

        if (user == null || !user.getPassword().equals(currentPassword)) {
            return false;
        }

        updateUserSalt(newPassword, user);
        return true;
    }

    /**
     * Updates cached password and salt of user.
     *
     * @param password - password without encoding
     * @param user     - target user
     */
    private void updateUserSalt(String password, User user) {
        CodePair codePair = codable.encode(password);
        user.setPassword(codePair.getValue());
        user.setSalt(codePair.getSalt());
        USER_DAO.update(user);
    }

}
