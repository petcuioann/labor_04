package com.example.library;

import com.example.library.model.Author;
import com.example.library.model.BookDetails;
import com.example.library.model.Library;
import com.example.library.model.MagazineDetails;
import com.example.library.model.Member;
import com.example.library.model.ReadableItem;
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

    public DataInitializer(
            LibraryService libraryService,
            CatalogService catalogService,
            CirculationService circulationService) {
        this.libraryService = libraryService;
        this.catalogService = catalogService;
        this.circulationService = circulationService;
    }

    @Override
    public void run(String... args) {
        Library central = libraryService.saveLibrary(
                new Library("lib-1", "Central Library", "Str. Universitatii 1"));
        Library science = libraryService.saveLibrary(
                new Library("lib-2", "Science Branch", "Str. Mihail Kogalniceanu 4"));

        Author orwell = catalogService.saveAuthor(new Author("auth-1", "George Orwell"));
        Author tolkien = catalogService.saveAuthor(new Author("auth-2", "J.R.R. Tolkien"));
        Author martin = catalogService.saveAuthor(new Author("auth-3", "Robert C. Martin"));

        BookDetails book1 = catalogService.saveBook(
                new BookDetails("book-1", "1984", "978-0451524935", 328));
        BookDetails book2 = catalogService.saveBook(
                new BookDetails("book-2", "The Hobbit", "978-0547928227", 310));
        BookDetails book3 = catalogService.saveBook(
                new BookDetails("book-3", "Clean Code", "978-0132350884", 464));
        catalogService.saveMagazine(
                new MagazineDetails("mag-1", "Nature Vol. 612", "Springer Nature", 612));

        catalogService.linkAuthor(book1.getId(), orwell.getId());
        catalogService.linkAuthor(book2.getId(), tolkien.getId());
        catalogService.linkAuthor(book3.getId(), martin.getId());

        ReadableItem item1 = catalogService.addCopy(
                new ReadableItem("item-1", book1.getId(), "BC-101"), central.getId());
        ReadableItem item2 = catalogService.addCopy(
                new ReadableItem("item-2", book2.getId(), "BC-102"), central.getId());
        catalogService.addCopy(
                new ReadableItem("item-3", book3.getId(), "BC-103"), central.getId());
        catalogService.addCopy(
                new ReadableItem("item-4", book3.getId(), "BC-104"), science.getId());

        Member member1 = libraryService.registerMember(
                new Member("mem-1", "Ioan Petcu", "ioan.petcu@stud.ubbcluj.ro", central.getId()));
        Member member2 = libraryService.registerMember(
                new Member("mem-2", "Andrei Emanuel", "andrei.emanuel@stud.ubbcluj.ro", central.getId()));

        circulationService.borrow(
                member1.getId(), List.of(item1.getId()), LocalDate.now().plusDays(14));
        circulationService.reserve(member2.getId(), item2.getId());
    }
}
