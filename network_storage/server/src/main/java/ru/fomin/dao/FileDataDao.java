package ru.fomin.dao;

import org.hibernate.Session;
import ru.fomin.entities.Directory;
import ru.fomin.entities.FileData;

public class FileDataDao {

    public void create(FileData file) {
        Session session = SessionFactory.getSession();
        session.beginTransaction();
        session.save(file);
        session.getTransaction().commit();
        session.close();
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
