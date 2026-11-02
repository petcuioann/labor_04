package com.example.library.repository;

import com.example.library.model.Identifiable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryRepository<T extends Identifiable> implements CrudRepository<T> {
    private final Map<String, T> storage = new HashMap<>();

    @Override
    public T save(T entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId(UUID.randomUUID().toString());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public T findById(String id) {
        return storage.get(id);
    }

    @Override
    public boolean delete(String id) {
        return storage.remove(id) != null;
    }
}
