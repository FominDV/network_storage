package ru.fomin.dao;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.fomin.entities.User;

import javax.persistence.NoResultException;
import java.util.List;

public class UserDao {

    public static void create(String login, String password) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        User user = new User(login, password);
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    public User getUsersByLogin(String login){
       try(Session session = SessionFactory.getSession()) {
           Query query=session.createQuery("select i from User i where i.login=:login",User.class);
           query.setParameter("login",login);
            return  (User) query.getSingleResult();
       }catch (NoResultException e){
            return null;
       }

    }
}
