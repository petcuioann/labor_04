package com.example.library.repository;

import com.example.library.model.Author;
import com.example.library.model.BookAuthor;
import com.example.library.model.Library;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.model.Publication;
import com.example.library.model.ReadableItem;
import com.example.library.model.Reservation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public CrudRepository<Library> libraryRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public CrudRepository<Member> memberRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public CrudRepository<Loan> loanRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public CrudRepository<Reservation> reservationRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public CrudRepository<Publication> publicationRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public CrudRepository<ReadableItem> readableItemRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public CrudRepository<Author> authorRepository() {
        return new InMemoryRepository<>();
    }

    @Bean
    public CrudRepository<BookAuthor> bookAuthorRepository() {
        return new InMemoryRepository<>();
    }
}
