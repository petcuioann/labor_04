package com.example.library.repository;

import com.example.library.model.Author;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository extends InFileRepository<Author> {
    public AuthorRepository() {
        super("src/main/resources/data/authors.json", Author.class);
    }
}
