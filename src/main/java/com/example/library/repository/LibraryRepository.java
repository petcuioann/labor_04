package com.example.library.repository;

import com.example.library.model.Library;
import org.springframework.stereotype.Repository;

@Repository
public class LibraryRepository extends InFileRepository<Library> {
    public LibraryRepository() {
        super("src/main/resources/data/libraries.json", Library.class);
    }
}
