package com.example.library.repository;

import com.example.library.model.Identifiable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface CrudRepository<T extends Identifiable> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(String id);
    List<T> findBy(Predicate<? super T> filter);
    boolean delete(String id);
}
