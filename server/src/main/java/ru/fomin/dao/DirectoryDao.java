package ru.fomin.dao;

import org.hibernate.Session;
import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;

import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

public class DirectoryDao {

    /**
     * Create new directory.
     *
     * @param - new directory for creating
     * @return - id of new directory
     */
    public Long create(Directory directory) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.save(directory);
        Query query = session.createQuery("select max(d.id) from Directory d", Long.class);
        Long id = (Long) query.getSingleResult();
        session.getTransaction().commit();
        session.close();
        return id;
    }

    public List<FileData> getFiles(Long id) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        Query query = session.createQuery("select d.files from Directory d where d.id=:id", Collection.class);
        query.setParameter("id", id);
        List<FileData> files = query.getResultList();
        session.getTransaction().commit();
        session.close();
        return files;
    }

    public List<Directory> getNestedDirectories(Long id) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        Query query = session.createQuery("select d.nestedDirectories from Directory d where d.id=:id", Collection.class);
        query.setParameter("id", id);
        List<Directory> files = query.getResultList();
        session.getTransaction().commit();
        session.close();
        return files;
    }

    public Directory getDirectoryById(Long id) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        Directory directory = session.get(Directory.class, id);
        session.getTransaction().commit();
        session.close();
        return directory;
    }

    public void delete(Directory directory) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.delete(directory);
        session.getTransaction().commit();
        session.close();
    }

    public void update(Directory directory) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.update(directory);
        session.getTransaction().commit();
        session.close();
    }
}
