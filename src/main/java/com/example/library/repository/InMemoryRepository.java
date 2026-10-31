package com.example.library.repository;

import com.example.library.model.Identifiable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class InMemoryRepository<T extends Identifiable> implements CrudRepository<T> {
    private final Map<String, T> storage = new ConcurrentHashMap<>();

    @Override public T save(T entity) {
        if (entity == null) throw new IllegalArgumentException("entity must not be null");
        if (entity.getId() == null || entity.getId().isBlank()) entity.setId(UUID.randomUUID().toString());
        storage.put(entity.getId(), entity);
        return entity;
    }
    @Override public List<T> findAll() { return List.copyOf(storage.values()); }
    @Override public Optional<T> findById(String id) { return id == null ? Optional.empty() : Optional.ofNullable(storage.get(id)); }
    @Override public List<T> findBy(Predicate<? super T> filter) { return storage.values().stream().filter(filter).toList(); }
    @Override public boolean delete(String id) { return id != null && storage.remove(id) != null; }
}
