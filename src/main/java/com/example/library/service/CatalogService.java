package com.example.library.service;

import com.example.library.model.Author;
import com.example.library.model.BookAuthor;
import com.example.library.model.BookDetails;
import com.example.library.model.Library;
import com.example.library.model.MagazineDetails;
import com.example.library.model.Publication;
import com.example.library.model.ReadableItem;
import com.example.library.repository.CrudRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private final CrudRepository<Publication> publications;
    private final CrudRepository<ReadableItem> items;
    private final CrudRepository<Author> authors;
    private final CrudRepository<BookAuthor> bookAuthors;
    private final CrudRepository<Library> libraries;

    public CatalogService(
            CrudRepository<Publication> publications,
            CrudRepository<ReadableItem> items,
            CrudRepository<Author> authors,
            CrudRepository<BookAuthor> bookAuthors,
            CrudRepository<Library> libraries) {
        this.publications = publications;
        this.items = items;
        this.authors = authors;
        this.bookAuthors = bookAuthors;
        this.libraries = libraries;
    }

    public BookDetails saveBook(BookDetails book) {
        publications.save(book);
        return book;
    }

    public MagazineDetails saveMagazine(MagazineDetails magazine) {
        publications.save(magazine);
        return magazine;
    }

    public List<Publication> findAllPublications() {
        return publications.findAll();
    }

    public Publication findPublication(String id) {
        return publications.findById(id);
    }

    public List<BookDetails> findAllBooks() {
        List<BookDetails> result = new ArrayList<>();
        for (Publication publication : publications.findAll()) {
            if (publication instanceof BookDetails) {
                result.add((BookDetails) publication);
            }
        }
        return result;
    }

    public List<MagazineDetails> findAllMagazines() {
        List<MagazineDetails> result = new ArrayList<>();
        for (Publication publication : publications.findAll()) {
            if (publication instanceof MagazineDetails) {
                result.add((MagazineDetails) publication);
            }
        }
        return result;
    }

    public boolean deletePublication(String id) {
        return publications.delete(id);
    }

    public ReadableItem addCopy(ReadableItem item, String libraryId) {
        item.setStatus(ReadableItem.Status.AVAILABLE);
        ReadableItem savedItem = items.save(item);

        Publication publication = publications.findById(item.getPublicationId());
        if (publication != null) {
            publication.getCopies().add(savedItem);
        }

        Library library = libraries.findById(libraryId);
        if (library != null) {
            library.getReadableItems().add(savedItem);
        }

        return savedItem;
    }

    public List<ReadableItem> findAllItems() {
        return items.findAll();
    }

    public ReadableItem findItem(String id) {
        return items.findById(id);
    }

    public List<ReadableItem> findItemsByStatus(ReadableItem.Status status) {
        List<ReadableItem> result = new ArrayList<>();
        for (ReadableItem item : items.findAll()) {
            if (item.getStatus() == status) {
                result.add(item);
            }
        }
        return result;
    }

    public boolean deleteItem(String id) {
        ReadableItem item = items.findById(id);
        if (item == null) {
            return false;
        }
        Publication publication = publications.findById(item.getPublicationId());
        if (publication != null) {
            publication.getCopies().remove(item);
        }
        for (Library library : libraries.findAll()) {
            library.getReadableItems().remove(item);
        }
        return items.delete(id);
    }

    public Author saveAuthor(Author author) {
        return authors.save(author);
    }

    public List<Author> findAllAuthors() {
        return authors.findAll();
    }

    public Author findAuthor(String id) {
        return authors.findById(id);
    }

    public boolean deleteAuthor(String id) {
        return authors.delete(id);
    }

    public BookAuthor linkAuthor(String bookId, String authorId) {
        Publication publication = publications.findById(bookId);
        Author author = authors.findById(authorId);
        if (!(publication instanceof BookDetails) || author == null) {
            return null;
        }
        BookDetails book = (BookDetails) publication;
        BookAuthor savedLink = bookAuthors.save(new BookAuthor(null, bookId, authorId));
        book.getBookAuthors().add(savedLink);
        author.getBooks().add(savedLink);
        return savedLink;
    }

    public List<BookAuthor> findAllLinks() {
        return bookAuthors.findAll();
    }

    public boolean unlinkAuthor(String linkId) {
        BookAuthor link = bookAuthors.findById(linkId);
        if (link == null) {
            return false;
        }
        Publication publication = publications.findById(link.getBookId());
        if (publication instanceof BookDetails) {
            ((BookDetails) publication).getBookAuthors().remove(link);
        }
        Author author = authors.findById(link.getAuthorId());
        if (author != null) {
            author.getBooks().remove(link);
        }
        return bookAuthors.delete(linkId);
    }
}
