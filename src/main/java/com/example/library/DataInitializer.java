package com.example.library;

import com.example.library.model.Author;
import com.example.library.model.BookAuthor;
import com.example.library.model.BookDetails;
import com.example.library.model.Library;
import com.example.library.model.Loan;
import com.example.library.model.MagazineDetails;
import com.example.library.model.Member;
import com.example.library.model.ReadableItem;
import com.example.library.model.Reservation;
import com.example.library.repository.CrudRepository;
import com.example.library.service.CatalogService;
import com.example.library.service.CirculationService;
import com.example.library.service.LibraryService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final LibraryService libraryService;
    private final CatalogService catalogService;
    private final CirculationService circulationService;
    private final CrudRepository<BookAuthor> bookAuthorRepository;
    private final CrudRepository<Loan> loanRepository;
    private final CrudRepository<Reservation> reservationRepository;

    public DataInitializer(
            LibraryService libraryService,
            CatalogService catalogService,
            CirculationService circulationService,
            CrudRepository<BookAuthor> bookAuthorRepository,
            CrudRepository<Loan> loanRepository,
            CrudRepository<Reservation> reservationRepository) {
        this.libraryService = libraryService;
        this.catalogService = catalogService;
        this.circulationService = circulationService;
        this.bookAuthorRepository = bookAuthorRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void run(String... args) {
        if (!libraryService.findAllLibraries().isEmpty()) {
            return;
        }

        // 10 Libraries (lib-10 has no members/items for easy delete testing)
        String[][] libs = {
            {"lib-1", "Central University Library", "Str. Clinicilor 2"},
            {"lib-2", "Mathematics & CS Branch", "Str. Mihail Kogalniceanu 1"},
            {"lib-3", "Observator Campus Library", "Str. Observatorului 34"},
            {"lib-4", "Hasdeu Student Reading Room", "Str. Bogdan Petriceicu Hasdeu 45"},
            {"lib-5", "Physics & Chemistry Branch", "Str. Arany Janos 11"},
            {"lib-6", "History & Philosophy Archive", "Str. Napoca 15"},
            {"lib-7", "Letters & Linguistics Library", "Str. Horea 31"},
            {"lib-8", "Economics & Business Hub", "Str. Teodor Mihali 58"},
            {"lib-9", "Law Faculty Reference Library", "Str. Avram Iancu 11"},
            {"lib-10", "Digital Annex Branch", "Bd. 21 Decembrie 1989 104"}
        };
        for (String[] l : libs) {
            libraryService.saveLibrary(new Library(l[0], l[1], l[2]));
        }

        // 10 Authors (auth-10 has no linked books for easy delete testing)
        String[][] authors = {
            {"auth-1", "George Orwell"},
            {"auth-2", "J.R.R. Tolkien"},
            {"auth-3", "Robert C. Martin"},
            {"auth-4", "Martin Fowler"},
            {"auth-5", "Joshua Bloch"},
            {"auth-6", "Donald E. Knuth"},
            {"auth-7", "Thomas H. Cormen"},
            {"auth-8", "Erich Gamma"},
            {"auth-9", "Brian Goetz"},
            {"auth-10", "Bjarne Stroustrup"}
        };
        for (String[] a : authors) {
            catalogService.saveAuthor(new Author(a[0], a[1]));
        }

        // 10 Books + 2 Magazines = 12 Publications (book-10 has no copies)
        Object[][] books = {
            {"book-1", "1984", "978-0451524935", 328},
            {"book-2", "The Hobbit", "978-0547928227", 310},
            {"book-3", "Clean Code", "978-0132350884", 464},
            {"book-4", "Refactoring", "978-0134757599", 448},
            {"book-5", "Effective Java", "978-0134685991", 416},
            {"book-6", "The Art of Computer Programming Vol. 1", "978-0201896831", 672},
            {"book-7", "Introduction to Algorithms", "978-0262046305", 1312},
            {"book-8", "Design Patterns", "978-0201633610", 395},
            {"book-9", "Java Concurrency in Practice", "978-0321349606", 432},
            {"book-10", "The C++ Programming Language", "978-0321563842", 1376}
        };
        for (Object[] b : books) {
            catalogService.saveBook(new BookDetails((String) b[0], (String) b[1], (String) b[2], (Integer) b[3]));
        }
        catalogService.saveMagazine(new MagazineDetails("mag-1", "Nature Vol. 612", "Springer Nature", 612));
        catalogService.saveMagazine(new MagazineDetails("mag-2", "IEEE Software Vol. 41", "IEEE Computer Society", 41));

        // 10 BookAuthor links (ba-1 .. ba-10)
        String[][] links = {
            {"ba-1", "book-1", "auth-1"},
            {"ba-2", "book-2", "auth-2"},
            {"ba-3", "book-3", "auth-3"},
            {"ba-4", "book-4", "auth-4"},
            {"ba-5", "book-5", "auth-5"},
            {"ba-6", "book-6", "auth-6"},
            {"ba-7", "book-7", "auth-7"},
            {"ba-8", "book-8", "auth-8"},
            {"ba-9", "book-9", "auth-9"},
            {"ba-10", "book-8", "auth-4"}
        };
        for (String[] lk : links) {
            BookAuthor savedLink = bookAuthorRepository.save(new BookAuthor(lk[0], lk[1], lk[2]));
            BookDetails book = (BookDetails) catalogService.findPublication(lk[1]);
            Author author = catalogService.findAuthor(lk[2]);
            book.getBookAuthors().add(savedLink);
            catalogService.saveBook(book);
            author.getBooks().add(savedLink);
            catalogService.saveAuthor(author);
        }

        // 14 ReadableItems across book-1..book-9 and lib-1..lib-9
        String[][] copies = {
            {"item-1", "book-1", "BC-101", "lib-1"},
            {"item-2", "book-2", "BC-102", "lib-1"},
            {"item-3", "book-3", "BC-103", "lib-2"},
            {"item-4", "book-4", "BC-104", "lib-2"},
            {"item-5", "book-5", "BC-105", "lib-3"},
            {"item-6", "book-6", "BC-106", "lib-3"},
            {"item-7", "book-7", "BC-107", "lib-4"},
            {"item-8", "book-8", "BC-108", "lib-5"},
            {"item-9", "book-9", "BC-109", "lib-6"},
            {"item-10", "book-1", "BC-110", "lib-7"},
            {"item-11", "book-2", "BC-111", "lib-8"},
            {"item-12", "book-3", "BC-112", "lib-9"},
            {"item-13", "book-4", "BC-113", "lib-1"},
            {"item-14", "book-5", "BC-114", "lib-2"}
        };
        for (String[] c : copies) {
            catalogService.addCopy(new ReadableItem(c[0], c[1], c[2]), c[3]);
        }

        Member member1 = libraryService.registerMember(
                new Member("mem-1", "Ioan Petcu", "ioan.petcu@stud.ubbcluj.ro", "lib-1"));
        Member member2 = libraryService.registerMember(
                new Member("mem-2", "Andrei Emanuel", "andrei.emanuel@stud.ubbcluj.ro", "lib-1"));

        circulationService.borrow(
                member1.getId(), List.of("item-1"), LocalDate.now().plusDays(14));
        circulationService.reserve(member2.getId(), "item-2");
    }
}
