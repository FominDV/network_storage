package ru.fomin.dao;

import org.hibernate.Session;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class DirectoryDao {

    public void create(Directory data) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.save(data);
        session.getTransaction().commit();
        session.close();
    }
}
