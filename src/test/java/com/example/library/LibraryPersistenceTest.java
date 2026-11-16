package com.example.library;

import com.example.library.model.Author;
import com.example.library.model.BookDetails;
import com.example.library.model.Library;
import com.example.library.model.Member;
import com.example.library.model.ReadableItem;
import com.example.library.repository.InFileRepository;
import com.example.library.service.CatalogService;
import com.example.library.service.CirculationService;
import com.example.library.service.LibraryService;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryPersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void persistsEntitiesAcrossRepositoryInstancesAndBlocksParentDeletion() {
        InFileRepository<Library> libRepo = new InFileRepository<>(tempDir.resolve("libraries.json").toString(), Library.class) {};
        InFileRepository<Member> memRepo = new InFileRepository<>(tempDir.resolve("members.json").toString(), Member.class) {};
        InFileRepository<com.example.library.model.Loan> loanRepo = new InFileRepository<>(tempDir.resolve("loans.json").toString(), com.example.library.model.Loan.class) {};
        InFileRepository<com.example.library.model.Reservation> resRepo = new InFileRepository<>(tempDir.resolve("reservations.json").toString(), com.example.library.model.Reservation.class) {};
        InFileRepository<com.example.library.model.Publication> pubRepo = new InFileRepository<>(tempDir.resolve("publications.json").toString(), com.example.library.model.Publication.class) {};
        InFileRepository<ReadableItem> itemRepo = new InFileRepository<>(tempDir.resolve("items.json").toString(), ReadableItem.class) {};
        InFileRepository<Author> authRepo = new InFileRepository<>(tempDir.resolve("authors.json").toString(), Author.class) {};
        InFileRepository<com.example.library.model.BookAuthor> baRepo = new InFileRepository<>(tempDir.resolve("ba.json").toString(), com.example.library.model.BookAuthor.class) {};

        LibraryService libraryService = new LibraryService(libRepo, memRepo, loanRepo, resRepo);
        CatalogService catalogService = new CatalogService(pubRepo, itemRepo, authRepo, baRepo, libRepo, loanRepo, resRepo);
        CirculationService circulationService = new CirculationService(loanRepo, resRepo, memRepo, itemRepo, pubRepo, libRepo);

        Library lib = libraryService.saveLibrary(new Library("lib-t1", "Test Lib", "Str. Test 1"));
        Member member = libraryService.registerMember(new Member("mem-t1", "Ioan Petcu", "ioan@test.ro", lib.getId()));
        BookDetails book = catalogService.saveBook(new BookDetails("book-t1", "Clean Code", "978-0132350884", 464));
        ReadableItem item = catalogService.addCopy(new ReadableItem("item-t1", book.getId(), "BC-T1"), lib.getId());
        circulationService.borrow(member.getId(), List.of(item.getId()), LocalDate.now().plusDays(7));

        assertThrows(IllegalStateException.class, () -> libraryService.deleteMember(member.getId()));
        assertThrows(IllegalStateException.class, () -> catalogService.deleteItem(item.getId()));
        assertThrows(IllegalStateException.class, () -> libraryService.deleteLibrary(lib.getId()));

        InFileRepository<com.example.library.model.Publication> reloadedPubRepo =
                new InFileRepository<>(tempDir.resolve("publications.json").toString(), com.example.library.model.Publication.class) {};
        assertEquals(1, reloadedPubRepo.findAll().size());
        assertTrue(reloadedPubRepo.findById("book-t1") instanceof BookDetails);
    }
}
