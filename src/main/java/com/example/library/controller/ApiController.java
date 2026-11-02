package com.example.library.controller;

import com.example.library.model.Author;
import com.example.library.model.BookAuthor;
import com.example.library.model.BookDetails;
import com.example.library.model.Library;
import com.example.library.model.Loan;
import com.example.library.model.MagazineDetails;
import com.example.library.model.Member;
import com.example.library.model.Publication;
import com.example.library.model.ReadableItem;
import com.example.library.model.Reservation;
import com.example.library.service.CatalogService;
import com.example.library.service.CirculationService;
import com.example.library.service.LibraryService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api")
public class ApiController {
    private final LibraryService libraryService;
    private final CatalogService catalogService;
    private final CirculationService circulationService;

    public ApiController(
            LibraryService libraryService,
            CatalogService catalogService,
            CirculationService circulationService) {
        this.libraryService = libraryService;
        this.catalogService = catalogService;
        this.circulationService = circulationService;
    }

    public static class LoanRequest {
        private String memberId;
        private List<String> itemIds;
        private LocalDate dueDate;

        public LoanRequest() {
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public List<String> getItemIds() {
            return itemIds;
        }

        public void setItemIds(List<String> itemIds) {
            this.itemIds = itemIds;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }
    }

    @GetMapping("/libraries")
    public List<Library> libraries() {
        return libraryService.findAllLibraries();
    }

    @GetMapping("/libraries/{id}")
    public Library library(@PathVariable String id) {
        return libraryService.findLibrary(id);
    }

    @PostMapping("/libraries")
    public Library createLibrary(@RequestBody Library library) {
        return libraryService.saveLibrary(library);
    }

    @DeleteMapping("/libraries/{id}")
    public boolean deleteLibrary(@PathVariable String id) {
        return libraryService.deleteLibrary(id);
    }

    @GetMapping("/members")
    public List<Member> members(@RequestParam(required = false) String libraryId) {
        if (libraryId == null) {
            return libraryService.findAllMembers();
        }
        return libraryService.findMembersByLibrary(libraryId);
    }

    @GetMapping("/members/{id}")
    public Member member(@PathVariable String id) {
        return libraryService.findMember(id);
    }

    @PostMapping("/members")
    public Member createMember(@RequestBody Member member) {
        return libraryService.registerMember(member);
    }

    @DeleteMapping("/members/{id}")
    public boolean deleteMember(@PathVariable String id) {
        return libraryService.deleteMember(id);
    }

    @GetMapping("/publications")
    public List<Publication> publications() {
        return catalogService.findAllPublications();
    }

    @GetMapping("/publications/{id}")
    public Publication publication(@PathVariable String id) {
        return catalogService.findPublication(id);
    }

    @DeleteMapping("/publications/{id}")
    public boolean deletePublication(@PathVariable String id) {
        return catalogService.deletePublication(id);
    }

    @GetMapping("/books")
    public List<BookDetails> books() {
        return catalogService.findAllBooks();
    }

    @PostMapping("/books")
    public BookDetails createBook(@RequestBody BookDetails book) {
        return catalogService.saveBook(book);
    }

    @GetMapping("/magazines")
    public List<MagazineDetails> magazines() {
        return catalogService.findAllMagazines();
    }

    @PostMapping("/magazines")
    public MagazineDetails createMagazine(@RequestBody MagazineDetails magazine) {
        return catalogService.saveMagazine(magazine);
    }

    @GetMapping("/items")
    public List<ReadableItem> items(@RequestParam(required = false) ReadableItem.Status status) {
        if (status == null) {
            return catalogService.findAllItems();
        }
        return catalogService.findItemsByStatus(status);
    }

    @GetMapping("/items/{id}")
    public ReadableItem item(@PathVariable String id) {
        return catalogService.findItem(id);
    }

    @PostMapping("/items")
    public ReadableItem createItem(@RequestBody ReadableItem item, @RequestParam String libraryId) {
        return catalogService.addCopy(item, libraryId);
    }

    @DeleteMapping("/items/{id}")
    public boolean deleteItem(@PathVariable String id) {
        return catalogService.deleteItem(id);
    }

    @GetMapping("/authors")
    public List<Author> authors() {
        return catalogService.findAllAuthors();
    }

    @GetMapping("/authors/{id}")
    public Author author(@PathVariable String id) {
        return catalogService.findAuthor(id);
    }

    @PostMapping("/authors")
    public Author createAuthor(@RequestBody Author author) {
        return catalogService.saveAuthor(author);
    }

    @DeleteMapping("/authors/{id}")
    public boolean deleteAuthor(@PathVariable String id) {
        return catalogService.deleteAuthor(id);
    }

    @GetMapping("/book-authors")
    public List<BookAuthor> links() {
        return catalogService.findAllLinks();
    }

    @PostMapping("/book-authors")
    public BookAuthor link(@RequestParam String bookId, @RequestParam String authorId) {
        return catalogService.linkAuthor(bookId, authorId);
    }

    @DeleteMapping("/book-authors/{id}")
    public boolean unlink(@PathVariable String id) {
        return catalogService.unlinkAuthor(id);
    }

    @GetMapping("/loans")
    public List<Loan> loans(
            @RequestParam(required = false) String memberId,
            @RequestParam(defaultValue = "false") boolean overdue) {
        if (overdue) {
            return circulationService.findOverdueLoans();
        }
        if (memberId == null) {
            return circulationService.findAllLoans();
        }
        return circulationService.findLoansByMember(memberId);
    }

    @GetMapping("/loans/{id}")
    public Loan loan(@PathVariable String id) {
        return circulationService.findLoan(id);
    }

    @PostMapping("/loans")
    public Loan borrow(@RequestBody LoanRequest request) {
        return circulationService.borrow(request.getMemberId(), request.getItemIds(), request.getDueDate());
    }

    @PostMapping("/loans/{id}/return")
    public Loan returnLoan(@PathVariable String id) {
        return circulationService.returnLoan(id);
    }

    @GetMapping("/reservations")
    public List<Reservation> reservations() {
        return circulationService.findAllReservations();
    }

    @GetMapping("/reservations/{id}")
    public Reservation reservation(@PathVariable String id) {
        return circulationService.findReservation(id);
    }

    @PostMapping("/reservations")
    public Reservation reserve(@RequestParam String memberId, @RequestParam String itemId) {
        return circulationService.reserve(memberId, itemId);
    }

    @PostMapping("/reservations/{id}/cancel")
    public Reservation cancel(@PathVariable String id) {
        return circulationService.cancelReservation(id);
    }
}
