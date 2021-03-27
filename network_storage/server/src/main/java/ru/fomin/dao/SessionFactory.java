package ru.fomin.dao;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;
import ru.fomin.entities.User;

public class SessionFactory {

    private static final org.hibernate.SessionFactory SESSION_FACTORY;

    static {
        SESSION_FACTORY = new Configuration().
                addAnnotatedClass(User.class).
                addAnnotatedClass(Directory.class).
                addAnnotatedClass(FileData.class).
                buildSessionFactory();
    }

    public static Session getSession() {
        return SESSION_FACTORY.openSession();
    }
}
