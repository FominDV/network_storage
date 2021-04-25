package ru.fomin.dao.impl;

import ru.fomin.dao.CommonDao;
import ru.fomin.dao.impl.TransactionExecutor;

public class CommonDaoImpl extends TransactionExecutor implements CommonDao {

    @Override
    public <T> Long save(T entity) {
        return executeTransaction(session -> (Long) session.save(entity));
    }

    @Override
    public void save(Object... entities) {
        executeTransaction(session -> {
            for (Object entity : entities) {
                session.save(entity);
            }
            return null;
        });
    }

    @Override
    public void update(Object... entities) {
        executeTransaction(session -> {
            for (Object entity : entities) {
                session.update(entity);
            }
            return null;
        });
    }

    @Override
    public <T> T getById(Long id, Class<T> clazz) {
        return executeTransaction(session -> session.get(clazz, id));
    }

    @Override
    public void delete(Object entity){
        executeTransaction(session -> {
            session.delete(entity);
            return null;
        });
    }
}
