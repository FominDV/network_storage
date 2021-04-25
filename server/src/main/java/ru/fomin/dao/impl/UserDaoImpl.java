package ru.fomin.dao.impl;

import org.hibernate.query.Query;
import ru.fomin.dao.UserDao;
import ru.fomin.entity.Directory;
import ru.fomin.entity.User;

public class UserDaoImpl extends CommonDaoImpl implements UserDao {

    @Override
    public User getUsersByLogin(String login) {
        return executeTransaction(session -> {
            Query query = session.createQuery("select i from User i where i.login=:login", User.class);
            query.setParameter("login", login);
            try {
                return (User) query.getSingleResult();
            } catch (Exception e) {
               return null;
            }
        });
    }

    @Override
    public Directory getRootDirectory(Long id) {
        return executeTransaction(session -> {
            Query query = session.createQuery("select u.rootDirectory from User u where u.id=:id", Directory.class);
            query.setParameter("id", id);
            return (Directory) query.getSingleResult();
        });
    }

}
