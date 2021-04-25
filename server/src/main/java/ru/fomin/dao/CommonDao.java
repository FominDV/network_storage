package ru.fomin.dao;

public interface CommonDao {

    <T> Long save(T entity);

    void save(Object... entities);

    void update(Object... entities);

    <T> T getById(Long id, Class<T> clazz);

    void delete(Object entity);

}
