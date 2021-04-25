package ru.fomin.dao;

import ru.fomin.entity.Directory;
import ru.fomin.entity.User;

public interface UserDao extends CommonDao {

    User getUsersByLogin(String login);

    Directory getRootDirectory(Long id);

}
