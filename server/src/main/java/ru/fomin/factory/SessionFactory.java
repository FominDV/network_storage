package ru.fomin.factory;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;
import ru.fomin.entity.User;

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
