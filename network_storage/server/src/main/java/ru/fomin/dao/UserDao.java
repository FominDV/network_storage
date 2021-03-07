package ru.fomin.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.fomin.entities.Directory;
import ru.fomin.entities.User;

import javax.persistence.NoResultException;

public class UserDao {

    public void create(User user) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

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
            Session session = SessionFactory.getSession();
            session.beginTransaction();
            session.save(user);
            session.save(dataRoot);
            session.getTransaction().commit();
            session.close();
        }

        public Directory getRootDirectory(Long id){
            try (Session session = SessionFactory.getSession()) {
                Query query = session.createQuery("select u.rootDirectory from User u where u.id=:id", Directory.class);
                query.setParameter("id",id);
                return (Directory) query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }

}
