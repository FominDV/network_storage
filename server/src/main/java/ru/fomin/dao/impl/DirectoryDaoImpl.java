package ru.fomin.dao.impl;

import ru.fomin.dao.DirectoryDao;
import ru.fomin.entity.Directory;
import ru.fomin.entity.FileData;

import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

public class DirectoryDaoImpl extends CommonDaoImpl implements DirectoryDao {

    @Override
    public List<FileData> getFiles(Long id) {
        return executeTransaction(session -> {
            Query query = session.createQuery("select d.files from Directory d where d.id=:id", Collection.class);
            query.setParameter("id", id);
            return query.getResultList();
        });
    }

    @Override
    public List<Directory> getNestedDirectories(Long id) {
        return executeTransaction(session -> {
            Query query = session.createQuery("select d.nestedDirectories from Directory d where d.id=:id", Collection.class);
            query.setParameter("id", id);
            return query.getResultList();
        });
    }

}
