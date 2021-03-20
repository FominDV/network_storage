package ru.fomin.dao;

import org.hibernate.Session;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;

import javax.persistence.Query;
import java.util.Collection;

public class FileDataDao {

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
        FileData fileData=session.get(FileData.class,id);
        session.getTransaction().commit();
        session.close();
        return fileData;
    }
}
