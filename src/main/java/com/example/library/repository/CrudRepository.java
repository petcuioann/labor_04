package com.example.library.repository;

import com.example.library.model.Identifiable;
import java.util.List;

public interface CrudRepository<T extends Identifiable> {
    T save(T entity);

    List<T> findAll();

    T findById(String id);

    boolean delete(String id);
}
