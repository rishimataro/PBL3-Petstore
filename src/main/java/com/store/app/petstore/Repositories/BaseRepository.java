package com.store.app.petstore.Repositories;

import javafx.concurrent.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    Task<List<T>> findAll() throws SQLException;
    Task<Optional<T>> findById(int id) throws SQLException;
    Task<Boolean> save(T entity) throws SQLException;
    Task<Boolean> update(T entity) throws SQLException;
    Task<Boolean> deleteById(int id) throws SQLException;
}
