package ru.fomin.dao;

import org.hibernate.Session;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DirectoryDao {

    public void create(Directory directory) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.save(directory);
        session.getTransaction().commit();
        session.close();
    }

    public List<FileData> getFiles(Long id) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        Query query = session.createQuery("select d.files from Directory d where d.id=:id", Collection.class);
        query.setParameter("id",id);
        List<FileData> files = query.getResultList();
        session.getTransaction().commit();
        session.close();
        return files;
    }

    public List<Directory> getNestedDirectories(Long id) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        Query query = session.createQuery("select d.nestedDirectories from Directory d where d.id=:id",Collection.class);
        query.setParameter("id",id);
        List<Directory> files = query.getResultList();
        session.getTransaction().commit();
        session.close();
        return files;
    }
}
