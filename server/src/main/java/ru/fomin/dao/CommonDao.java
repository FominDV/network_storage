package ru.fomin.dao;

public class CommonDao extends TransactionExecutor {

    public <T> T save(T entity) {
        return executeTransaction(session -> (T) session.save(entity));
    }

    public void save(Object... entities) {
        executeTransaction(session -> {
            for (Object entity : entities) {
                session.save(entity);
            }
            return null;
        });
    }

    public void update(Object... entities) {
        executeTransaction(session -> {
            for (Object entity : entities) {
                session.update(entity);
            }
            return null;
        });
    }

    public <T> T getById(Long id, Class<T> clazz) {
        return executeTransaction(session -> session.get(clazz, id));
    }

}
