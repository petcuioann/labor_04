package com.example.library.repository;

import com.example.library.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Bean CrudRepository<Library> libraryRepository() { return new InMemoryRepository<>(); }
    @Bean CrudRepository<Member> memberRepository() { return new InMemoryRepository<>(); }
    @Bean CrudRepository<Loan> loanRepository() { return new InMemoryRepository<>(); }
    @Bean CrudRepository<Reservation> reservationRepository() { return new InMemoryRepository<>(); }
    @Bean CrudRepository<Publication> publicationRepository() { return new InMemoryRepository<>(); }
    @Bean CrudRepository<ReadableItem> readableItemRepository() { return new InMemoryRepository<>(); }
    @Bean CrudRepository<Author> authorRepository() { return new InMemoryRepository<>(); }
    @Bean CrudRepository<BookAuthor> bookAuthorRepository() { return new InMemoryRepository<>(); }
}
