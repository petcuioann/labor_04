package com.example.library.repository;

import com.example.library.model.BookAuthor;
import org.springframework.stereotype.Repository;

@Repository
public class BookAuthorRepository extends InFileRepository<BookAuthor> {
    public BookAuthorRepository() {
        super("src/main/resources/data/book_authors.json", BookAuthor.class);
    }
}
