package com.example.library.controller;

import com.example.library.model.*;
import com.example.library.service.CatalogService;
import com.example.library.service.CirculationService;
import com.example.library.service.LibraryService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping("/api")
public class ApiController {
    private final LibraryService libraryService;
    private final CatalogService catalogService;
    private final CirculationService circulationService;

    public ApiController(LibraryService libraryService, CatalogService catalogService, CirculationService circulationService) {
        this.libraryService = libraryService; this.catalogService = catalogService; this.circulationService = circulationService;
    }

    public record LoanRequest(String memberId, List<String> itemIds, LocalDate dueDate) {}

    @GetMapping("/libraries") public List<Library> libraries() { return libraryService.findAllLibraries(); }
    @GetMapping("/libraries/{id}") public ResponseEntity<Library> library(@PathVariable String id) { return ResponseEntity.of(libraryService.findLibrary(id)); }
    @PostMapping("/libraries") @ResponseStatus(HttpStatus.CREATED) public Library createLibrary(@RequestBody Library library) { return libraryService.saveLibrary(library); }
    @DeleteMapping("/libraries/{id}") public ResponseEntity<Void> deleteLibrary(@PathVariable String id) { return noContent(libraryService.deleteLibrary(id)); }
    @GetMapping("/members") public List<Member> members(@RequestParam(required = false) String libraryId) { return libraryId == null ? libraryService.findAllMembers() : libraryService.findMembersByLibrary(libraryId); }
    @GetMapping("/members/{id}") public ResponseEntity<Member> member(@PathVariable String id) { return ResponseEntity.of(libraryService.findMember(id)); }
    @PostMapping("/members") @ResponseStatus(HttpStatus.CREATED) public Member createMember(@RequestBody Member member) { return libraryService.registerMember(member); }
    @DeleteMapping("/members/{id}") public ResponseEntity<Void> deleteMember(@PathVariable String id) { return noContent(libraryService.deleteMember(id)); }

    @GetMapping("/publications") public List<Publication> publications() { return catalogService.findAllPublications(); }
    @GetMapping("/publications/{id}") public ResponseEntity<Publication> publication(@PathVariable String id) { return ResponseEntity.of(catalogService.findPublication(id, Publication.class)); }
    @DeleteMapping("/publications/{id}") public ResponseEntity<Void> deletePublication(@PathVariable String id) { return noContent(catalogService.deletePublication(id)); }
    @GetMapping("/books") public List<BookDetails> books() { return catalogService.findPublications(BookDetails.class); }
    @PostMapping("/books") @ResponseStatus(HttpStatus.CREATED) public BookDetails createBook(@RequestBody BookDetails book) { return catalogService.savePublication(book); }
    @GetMapping("/magazines") public List<MagazineDetails> magazines() { return catalogService.findPublications(MagazineDetails.class); }
    @PostMapping("/magazines") @ResponseStatus(HttpStatus.CREATED) public MagazineDetails createMagazine(@RequestBody MagazineDetails magazine) { return catalogService.savePublication(magazine); }
    @GetMapping("/items") public List<ReadableItem> items(@RequestParam(required = false) ReadableItem.Status status) { return status == null ? catalogService.findAllItems() : catalogService.findItemsByStatus(status); }
    @GetMapping("/items/{id}") public ResponseEntity<ReadableItem> item(@PathVariable String id) { return ResponseEntity.of(catalogService.findItem(id)); }
    @PostMapping("/items") @ResponseStatus(HttpStatus.CREATED) public ReadableItem createItem(@RequestBody ReadableItem item, @RequestParam String libraryId) { return catalogService.addCopy(item, libraryId); }
    @DeleteMapping("/items/{id}") public ResponseEntity<Void> deleteItem(@PathVariable String id) { return noContent(catalogService.deleteItem(id)); }
    @GetMapping("/authors") public List<Author> authors() { return catalogService.findAllAuthors(); }
    @GetMapping("/authors/{id}") public ResponseEntity<Author> author(@PathVariable String id) { return ResponseEntity.of(catalogService.findAuthor(id)); }
    @PostMapping("/authors") @ResponseStatus(HttpStatus.CREATED) public Author createAuthor(@RequestBody Author author) { return catalogService.saveAuthor(author); }
    @DeleteMapping("/authors/{id}") public ResponseEntity<Void> deleteAuthor(@PathVariable String id) { return noContent(catalogService.deleteAuthor(id)); }
    @GetMapping("/book-authors") public List<BookAuthor> links() { return catalogService.findAllLinks(); }
    @PostMapping("/book-authors") @ResponseStatus(HttpStatus.CREATED) public BookAuthor link(@RequestParam String bookId, @RequestParam String authorId) { return catalogService.linkAuthor(bookId, authorId); }
    @DeleteMapping("/book-authors/{id}") public ResponseEntity<Void> unlink(@PathVariable String id) { return noContent(catalogService.unlinkAuthor(id)); }

    @GetMapping("/loans") public List<Loan> loans(@RequestParam(required = false) String memberId, @RequestParam(defaultValue = "false") boolean overdue) {
        if (overdue) return circulationService.findOverdueLoans();
        return memberId == null ? circulationService.findAllLoans() : circulationService.findLoansByMember(memberId);
    }
    @GetMapping("/loans/{id}") public ResponseEntity<Loan> loan(@PathVariable String id) { return ResponseEntity.of(circulationService.findLoan(id)); }
    @PostMapping("/loans") @ResponseStatus(HttpStatus.CREATED) public Loan borrow(@RequestBody LoanRequest request) { return circulationService.borrow(request.memberId(), request.itemIds(), request.dueDate()); }
    @PostMapping("/loans/{id}/return") public Loan returnLoan(@PathVariable String id) { return circulationService.returnLoan(id); }
    @GetMapping("/reservations") public List<Reservation> reservations() { return circulationService.findAllReservations(); }
    @GetMapping("/reservations/{id}") public ResponseEntity<Reservation> reservation(@PathVariable String id) { return ResponseEntity.of(circulationService.findReservation(id)); }
    @PostMapping("/reservations") @ResponseStatus(HttpStatus.CREATED) public Reservation reserve(@RequestParam String memberId, @RequestParam String itemId) { return circulationService.reserve(memberId, itemId); }
    @PostMapping("/reservations/{id}/cancel") public Reservation cancel(@PathVariable String id) { return circulationService.cancelReservation(id); }

    @ExceptionHandler(NoSuchElementException.class) @ResponseStatus(HttpStatus.NOT_FOUND) public Map<String, String> notFound(RuntimeException e) { return Map.of("error", e.getMessage()); }
    @ExceptionHandler(IllegalStateException.class) @ResponseStatus(HttpStatus.CONFLICT) public Map<String, String> conflict(RuntimeException e) { return Map.of("error", e.getMessage()); }
    @ExceptionHandler(IllegalArgumentException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String, String> badRequest(RuntimeException e) { return Map.of("error", e.getMessage()); }

    private static ResponseEntity<Void> noContent(boolean deleted) { return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build(); }
}
