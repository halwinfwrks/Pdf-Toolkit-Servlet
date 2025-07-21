package com.dev.dao;

import java.util.List;

public interface MysqlRepository<T> {
    int save(T entity);

    T findById(int id);

    void update(T entity);

    void delete(int id);

    List<T> findAll();
}
