package com.example.library.repository;

import com.example.library.model.Publication;
import org.springframework.stereotype.Repository;

@Repository
public class PublicationRepository extends InFileRepository<Publication> {
    public PublicationRepository() {
        super("src/main/resources/data/publications.json", Publication.class);
    }
}
