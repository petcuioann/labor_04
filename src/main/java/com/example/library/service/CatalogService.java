package com.example.library.service;

import com.example.library.model.Author;
import com.example.library.model.BookAuthor;
import com.example.library.model.BookDetails;
import com.example.library.model.Library;
import com.example.library.model.Loan;
import com.example.library.model.MagazineDetails;
import com.example.library.model.Publication;
import com.example.library.model.ReadableItem;
import com.example.library.model.Reservation;
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
    private final CrudRepository<Loan> loans;
    private final CrudRepository<Reservation> reservations;

    public CatalogService(
            CrudRepository<Publication> publications,
            CrudRepository<ReadableItem> items,
            CrudRepository<Author> authors,
            CrudRepository<BookAuthor> bookAuthors,
            CrudRepository<Library> libraries,
            CrudRepository<Loan> loans,
            CrudRepository<Reservation> reservations) {
        this.publications = publications;
        this.items = items;
        this.authors = authors;
        this.bookAuthors = bookAuthors;
        this.libraries = libraries;
        this.loans = loans;
        this.reservations = reservations;
    }

    public BookDetails saveBook(BookDetails book) {
        if (book.getId() != null && !book.getId().isBlank()) {
            Publication existing = publications.findById(book.getId());
            if (existing != null && existing != book) {
                if (book.getCopies().isEmpty()) {
                    book.getCopies().addAll(existing.getCopies());
                }
                if (existing instanceof BookDetails existingBook && book.getBookAuthors().isEmpty()) {
                    book.getBookAuthors().addAll(existingBook.getBookAuthors());
                }
            }
        }
        publications.save(book);
        return book;
    }

    public MagazineDetails saveMagazine(MagazineDetails magazine) {
        if (magazine.getId() != null && !magazine.getId().isBlank()) {
            Publication existing = publications.findById(magazine.getId());
            if (existing != null && existing != magazine && magazine.getCopies().isEmpty()) {
                magazine.getCopies().addAll(existing.getCopies());
            }
        }
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
            if (publication instanceof BookDetails book) {
                result.add(book);
            }
        }
        return result;
    }

    public List<MagazineDetails> findAllMagazines() {
        List<MagazineDetails> result = new ArrayList<>();
        for (Publication publication : publications.findAll()) {
            if (publication instanceof MagazineDetails mag) {
                result.add(mag);
            }
        }
        return result;
    }

    public boolean deletePublication(String id) {
        Publication publication = publications.findById(id);
        if (publication == null) {
            return false;
        }
        boolean hasCopies = !publication.getCopies().isEmpty()
                || items.findAll().stream().anyMatch(i -> id.equals(i.getPublicationId()));
        if (hasCopies) {
            throw new IllegalStateException(
                    "Cannot delete publication '" + publication.getTitle()
                            + "' because it still has readable item copies. Delete child items first.");
        }
        if (publication instanceof BookDetails book) {
            for (BookAuthor link : new ArrayList<>(book.getBookAuthors())) {
                unlinkAuthor(link.getId());
            }
            for (BookAuthor link : bookAuthors.findAll()) {
                if (id.equals(link.getBookId())) {
                    unlinkAuthor(link.getId());
                }
            }
        }
        return publications.delete(id);
    }

    public ReadableItem addCopy(ReadableItem item, String libraryId) {
        if (item.getStatus() == null) {
            item.setStatus(ReadableItem.Status.AVAILABLE);
        }
        ReadableItem savedItem = items.save(item);

        Publication publication = publications.findById(savedItem.getPublicationId());
        if (publication != null) {
            publication.getCopies().removeIf(c -> savedItem.getId().equals(c.getId()));
            publication.getCopies().add(savedItem);
            publications.save(publication);
        }

        if (libraryId != null && !libraryId.isBlank()) {
            Library library = libraries.findById(libraryId);
            if (library != null) {
                library.getReadableItems().removeIf(c -> savedItem.getId().equals(c.getId()));
                library.getReadableItems().add(savedItem);
                libraries.save(library);
            }
        }

        return savedItem;
    }

    public ReadableItem updateCopy(ReadableItem item, String libraryId) {
        ReadableItem existing = item.getId() != null ? items.findById(item.getId()) : null;
        String oldPublicationId = existing != null ? existing.getPublicationId() : null;
        if (existing != null && item.getStatus() == null) {
            item.setStatus(existing.getStatus());
        }
        ReadableItem savedItem = items.save(item);

        if (oldPublicationId != null && !oldPublicationId.equals(savedItem.getPublicationId())) {
            Publication oldPub = publications.findById(oldPublicationId);
            if (oldPub != null) {
                oldPub.getCopies().removeIf(c -> savedItem.getId().equals(c.getId()));
                publications.save(oldPub);
            }
        }

        Publication newPub = publications.findById(savedItem.getPublicationId());
        if (newPub != null) {
            newPub.getCopies().removeIf(c -> savedItem.getId().equals(c.getId()));
            newPub.getCopies().add(savedItem);
            publications.save(newPub);
        }

        for (Library lib : libraries.findAll()) {
            boolean removed = lib.getReadableItems().removeIf(c -> savedItem.getId().equals(c.getId()));
            if (libraryId != null && libraryId.equals(lib.getId())) {
                lib.getReadableItems().add(savedItem);
                libraries.save(lib);
            } else if (removed) {
                libraries.save(lib);
            }
        }

        return savedItem;
    }

    public String findLibraryIdForItem(String itemId) {
        if (itemId == null) {
            return null;
        }
        for (Library lib : libraries.findAll()) {
            for (ReadableItem copy : lib.getReadableItems()) {
                if (itemId.equals(copy.getId())) {
                    return lib.getId();
                }
            }
        }
        return null;
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
        boolean inLoan = item.getStatus() == ReadableItem.Status.BORROWED
                || loans.findAll().stream()
                        .anyMatch(l -> l.getItems().stream().anyMatch(i -> id.equals(i.getId())));
        boolean inReservation = item.getStatus() == ReadableItem.Status.RESERVED
                || reservations.findAll().stream()
                        .anyMatch(r -> id.equals(r.getReadableItemId()));
        if (inLoan || inReservation) {
            throw new IllegalStateException(
                    "Cannot delete readable item '" + item.getBarcode()
                            + "' because it is referenced by a loan or reservation. Delete the loan/reservation first.");
        }

        Publication publication = publications.findById(item.getPublicationId());
        if (publication != null) {
            publication.getCopies().removeIf(c -> id.equals(c.getId()));
            publications.save(publication);
        }
        for (Library library : libraries.findAll()) {
            if (library.getReadableItems().removeIf(c -> id.equals(c.getId()))) {
                libraries.save(library);
            }
        }
        return items.delete(id);
    }

    public Author saveAuthor(Author author) {
        if (author.getId() != null && !author.getId().isBlank()) {
            Author existing = authors.findById(author.getId());
            if (existing != null && existing != author && author.getBooks().isEmpty()) {
                author.getBooks().addAll(existing.getBooks());
            }
        }
        return authors.save(author);
    }

    public List<Author> findAllAuthors() {
        return authors.findAll();
    }

    public Author findAuthor(String id) {
        return authors.findById(id);
    }

    public boolean deleteAuthor(String id) {
        Author author = authors.findById(id);
        if (author == null) {
            return false;
        }
        boolean hasBooks = !author.getBooks().isEmpty()
                || bookAuthors.findAll().stream().anyMatch(ba -> id.equals(ba.getAuthorId()));
        if (hasBooks) {
            throw new IllegalStateException(
                    "Cannot delete author '" + author.getName()
                            + "' because they still have linked books. Delete or unlink their books first.");
        }
        return authors.delete(id);
    }

    public BookAuthor linkAuthor(String bookId, String authorId) {
        Publication publication = publications.findById(bookId);
        Author author = authors.findById(authorId);
        if (!(publication instanceof BookDetails book) || author == null) {
            return null;
        }
        for (BookAuthor existing : book.getBookAuthors()) {
            if (authorId.equals(existing.getAuthorId())) {
                return existing;
            }
        }
        BookAuthor savedLink = bookAuthors.save(new BookAuthor(null, bookId, authorId));
        book.getBookAuthors().add(savedLink);
        publications.save(book);

        author.getBooks().add(savedLink);
        authors.save(author);
        return savedLink;
    }

    public void updateBookPrimaryAuthor(String bookId, String authorId) {
        Publication publication = publications.findById(bookId);
        if (!(publication instanceof BookDetails book)) {
            return;
        }
        for (BookAuthor link : new ArrayList<>(book.getBookAuthors())) {
            unlinkAuthor(link.getId());
        }
        if (authorId != null && !authorId.isBlank()) {
            linkAuthor(bookId, authorId);
        }
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
        if (publication instanceof BookDetails book) {
            book.getBookAuthors().removeIf(ba -> linkId.equals(ba.getId()));
            publications.save(book);
        }
        Author author = authors.findById(link.getAuthorId());
        if (author != null) {
            author.getBooks().removeIf(ba -> linkId.equals(ba.getId()));
            authors.save(author);
        }
        return bookAuthors.delete(linkId);
    }
}
