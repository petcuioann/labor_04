package com.example.library.service;

import com.example.library.model.*;
import com.example.library.repository.CrudRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private final CrudRepository<Publication> publications;
    private final CrudRepository<ReadableItem> items;
    private final CrudRepository<Author> authors;
    private final CrudRepository<BookAuthor> bookAuthors;
    private final CrudRepository<Library> libraries;

    public CatalogService(CrudRepository<Publication> publications, CrudRepository<ReadableItem> items, CrudRepository<Author> authors,
                          CrudRepository<BookAuthor> bookAuthors, CrudRepository<Library> libraries) {
        this.publications = publications; this.items = items; this.authors = authors; this.bookAuthors = bookAuthors; this.libraries = libraries;
    }

    public <P extends Publication> P savePublication(P publication) {
        publication.getCopies().clear();
        if (publication instanceof BookDetails book) book.getBookAuthors().clear();
        publications.save(publication);
        return publication;
    }
    public List<Publication> findAllPublications() { return publications.findAll(); }
    public <P extends Publication> List<P> findPublications(Class<P> type) { return publications.findBy(type::isInstance).stream().map(type::cast).toList(); }
    public <P extends Publication> Optional<P> findPublication(String id, Class<P> type) { return publications.findById(id).filter(type::isInstance).map(type::cast); }
    public boolean deletePublication(String id) {
        Optional<Publication> publication = publications.findById(id);
        if (publication.isEmpty()) return false;
        if (!publication.get().getCopies().isEmpty()) throw new IllegalStateException("publication still has copies");
        if (publication.get() instanceof BookDetails book) List.copyOf(book.getBookAuthors()).forEach(ba -> unlinkAuthor(ba.getId()));
        return publications.delete(id);
    }

    public ReadableItem addCopy(ReadableItem item, String libraryId) {
        Publication publication = require(publications, item.getPublicationId(), "publication");
        Library library = require(libraries, libraryId, "library");
        item.setStatus(ReadableItem.Status.AVAILABLE);
        items.save(item);
        publication.getCopies().add(item);
        library.getReadableItems().add(item);
        return item;
    }
    public List<ReadableItem> findAllItems() { return items.findAll(); }
    public Optional<ReadableItem> findItem(String id) { return items.findById(id); }
    public List<ReadableItem> findItemsByStatus(ReadableItem.Status status) { return items.findBy(i -> i.getStatus() == status); }
    public boolean deleteItem(String id) {
        Optional<ReadableItem> item = items.findById(id);
        if (item.isEmpty()) return false;
        if (!item.get().isAvailable()) throw new IllegalStateException("item is " + item.get().getStatus());
        publications.findById(item.get().getPublicationId()).ifPresent(p -> p.getCopies().removeIf(i -> i.getId().equals(id)));
        libraries.findAll().forEach(l -> l.getReadableItems().removeIf(i -> i.getId().equals(id)));
        return items.delete(id);
    }

    public Author saveAuthor(Author author) {
        author.getBooks().clear();
        return authors.save(author);
    }
    public List<Author> findAllAuthors() { return authors.findAll(); }
    public Optional<Author> findAuthor(String id) { return authors.findById(id); }
    public boolean deleteAuthor(String id) {
        authors.findById(id).ifPresent(a -> List.copyOf(a.getBooks()).forEach(ba -> unlinkAuthor(ba.getId())));
        return authors.delete(id);
    }

    public BookAuthor linkAuthor(String bookId, String authorId) {
        BookDetails book = findPublication(bookId, BookDetails.class).orElseThrow(() -> new NoSuchElementException("book not found: " + bookId));
        Author author = require(authors, authorId, "author");
        if (book.getBookAuthors().stream().anyMatch(ba -> ba.getAuthorId().equals(authorId))) throw new IllegalStateException("author already linked");
        BookAuthor link = bookAuthors.save(new BookAuthor(null, bookId, authorId));
        book.getBookAuthors().add(link);
        author.getBooks().add(link);
        return link;
    }
    public List<BookAuthor> findAllLinks() { return bookAuthors.findAll(); }
    public boolean unlinkAuthor(String linkId) {
        Optional<BookAuthor> link = bookAuthors.findById(linkId);
        if (link.isEmpty()) return false;
        findPublication(link.get().getBookId(), BookDetails.class).ifPresent(b -> b.getBookAuthors().removeIf(ba -> ba.getId().equals(linkId)));
        authors.findById(link.get().getAuthorId()).ifPresent(a -> a.getBooks().removeIf(ba -> ba.getId().equals(linkId)));
        return bookAuthors.delete(linkId);
    }

    static <T extends Identifiable> T require(CrudRepository<T> repository, String id, String name) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException(name + " not found: " + id));
    }
}
