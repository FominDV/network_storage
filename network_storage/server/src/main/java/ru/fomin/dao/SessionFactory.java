package ru.fomin.dao;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import ru.fomin.entities.User;

public class SessionFactory {

    private static final org.hibernate.SessionFactory SESSION_FACTORY;

    static {
        SESSION_FACTORY = new Configuration().
                addAnnotatedClass(User.class).
                buildSessionFactory();
    }

    public static org.hibernate.SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static Session getSession() {
        return SESSION_FACTORY.openSession();
    }
}
