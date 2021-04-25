package ru.fomin.dao;

import org.hibernate.Session;
import ru.fomin.entity.FileData;
import ru.fomin.factory.SessionFactory;

import javax.persistence.Query;

public class FileDataDao {

    /**
     * Creates new file and returns id of new file.
     */
    public Long create(FileData file) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.save(file);
        Query query = session.createQuery("select max(d.id) from FileData d", Long.class);
        Long id = (Long) query.getSingleResult();
        session.getTransaction().commit();
        session.close();
        return id;
    }

    public void deleteFile(FileData fileData) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.delete(fileData);
        session.getTransaction().commit();
        session.close();
    }

    public FileData getFile(Long id) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        FileData fileData = session.get(FileData.class, id);
        session.getTransaction().commit();
        session.close();
        return fileData;
    }

    public void updateFile(FileData fileData) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.update(fileData);
        session.getTransaction().commit();
        session.close();
    }
}
