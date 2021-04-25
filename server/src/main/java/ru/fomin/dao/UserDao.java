package ru.fomin.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.fomin.entity.Directory;
import ru.fomin.entity.User;
import ru.fomin.factory.SessionFactory;

import javax.persistence.NoResultException;

public class UserDao extends CommonDao{

    public User getUsersByLogin(String login) {
        try (Session session = SessionFactory.getSession()) {
            Query query = session.createQuery("select i from User i where i.login=:login", User.class);
            query.setParameter("login", login);
            return (User) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void create(User user, Directory dataRoot) {
        save(user, dataRoot);
    }

    public Directory getRootDirectory(Long id) {
        try (Session session = SessionFactory.getSession()) {
            Query query = session.createQuery("select u.rootDirectory from User u where u.id=:id", Directory.class);
            query.setParameter("id", id);
            return (Directory) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void updateUser(User user) {
       update(user);
    }

    public User findUserById(Long id) {
      return  getById(id, User.class);
    }
}
