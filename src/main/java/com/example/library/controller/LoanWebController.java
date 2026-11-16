package com.example.library.controller;

import com.example.library.model.Loan;
import com.example.library.model.ReadableItem;
import com.example.library.service.CatalogService;
import com.example.library.service.CirculationService;
import com.example.library.service.LibraryService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/loans")
public class LoanWebController {
    private final CirculationService circulationService;
    private final LibraryService libraryService;
    private final CatalogService catalogService;

    public LoanWebController(
            CirculationService circulationService,
            LibraryService libraryService,
            CatalogService catalogService) {
        this.circulationService = circulationService;
        this.libraryService = libraryService;
        this.catalogService = catalogService;
    }

    @GetMapping
    public String listLoans(Model model) {
        model.addAttribute("loans", circulationService.findAllLoans());
        return "loan/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("loan", new Loan());
        model.addAttribute("members", libraryService.findAllMembers());
        model.addAttribute("availableItems", catalogService.findItemsByStatus(ReadableItem.Status.AVAILABLE));
        model.addAttribute("defaultDueDate", LocalDate.now().plusDays(14));
        model.addAttribute("editMode", false);
        return "loan/form";
    }

    @PostMapping
    public String createLoan(
            @RequestParam String memberId,
            @RequestParam(required = false) List<String> itemIds,
            @RequestParam LocalDate dueDate) {
        circulationService.borrow(memberId, itemIds, dueDate);
        return "redirect:/loans";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable String id, Model model) {
        Loan loan = circulationService.findLoan(id);
        if (loan == null) {
            return "redirect:/loans";
        }
        model.addAttribute("loan", loan);
        return "loan/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Loan loan = circulationService.findLoan(id);
        if (loan == null) {
            return "redirect:/loans";
        }
        model.addAttribute("loan", loan);
        model.addAttribute("members", libraryService.findAllMembers());
        model.addAttribute("editMode", true);
        return "loan/form";
    }

    @PostMapping("/{id}/edit")
    public String updateLoan(
            @PathVariable String id,
            @RequestParam String memberId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam LocalDate dueDate,
            @RequestParam(required = false) LocalDate returnDate) {
        circulationService.updateLoan(id, memberId, date, dueDate, returnDate);
        return "redirect:/loans";
    }

    @PostMapping("/{id}/return")
    public String returnLoan(@PathVariable String id) {
        circulationService.returnLoan(id);
        return "redirect:/loans";
    }

    @PostMapping("/{id}/delete")
    public String deleteLoan(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            circulationService.deleteLoan(id);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/loans";
    }
}
