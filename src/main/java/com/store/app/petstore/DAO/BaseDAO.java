package com.store.app.petstore.DAO;

import java.util.List;

public interface BaseDAO<T> {
    List<T> getAll();
    T getById(int id);
    boolean create(T entity);
    boolean update(T entity);
    boolean delete(int id);
} 