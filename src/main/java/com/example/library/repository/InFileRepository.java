package com.example.library.repository;

import com.example.library.model.Identifiable;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class InFileRepository<T extends Identifiable> implements CrudRepository<T> {
    private final Path filePath;
    private final ObjectMapper objectMapper;
    private final JavaType listType;

    protected InFileRepository(String filePath, Class<T> entityClass) {
        this.filePath = Path.of(filePath);
        this.objectMapper = new ObjectMapper()
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.listType = this.objectMapper.getTypeFactory()
                .constructCollectionType(List.class, entityClass);
    }

    protected synchronized List<T> readAllFromFile() {
        try {
            if (!Files.exists(filePath) || Files.size(filePath) == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(filePath.toFile(), listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }

    protected synchronized void writeAllToFile(List<T> entities) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            objectMapper.writerFor(listType).writeValue(filePath.toFile(), entities);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }
}
