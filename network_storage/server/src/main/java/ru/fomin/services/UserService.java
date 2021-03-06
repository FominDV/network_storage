package ru.fomin.services;

import ru.fomin.dao.UserDao;
import ru.fomin.entities.User;

public class UserService {

    private final static UserDao USER_DAO = new UserDao();

    public static boolean isUserExist(String login) {
        if (USER_DAO.getUsersByLogin(login) == null) {
            return false;
        }
        return true;
    }

    public static boolean isValidUserData(String login, String password) {
        User user = USER_DAO.getUsersByLogin(login);
        return user != null && user.getPassword().equals(password);
    }
}
