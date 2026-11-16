package com.example.library.controller;

import com.example.library.model.BookDetails;
import com.example.library.model.Publication;
import com.example.library.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookWebController {
    private final CatalogService catalogService;

    public BookWebController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", catalogService.findAllBooks());
        return "book/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new BookDetails());
        model.addAttribute("authors", catalogService.findAllAuthors());
        model.addAttribute("selectedAuthorId", "");
        model.addAttribute("editMode", false);
        return "book/form";
    }

    @PostMapping
    public String createBook(
            @ModelAttribute("book") BookDetails book,
            @RequestParam(required = false) String authorId) {
        BookDetails saved = catalogService.saveBook(book);
        if (authorId != null && !authorId.isEmpty()) {
            catalogService.linkAuthor(saved.getId(), authorId);
        }
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable String id, Model model) {
        Publication pub = catalogService.findPublication(id);
        if (!(pub instanceof BookDetails book)) {
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        return "book/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Publication pub = catalogService.findPublication(id);
        if (!(pub instanceof BookDetails book)) {
            return "redirect:/books";
        }
        String selectedAuthorId = book.getBookAuthors().isEmpty()
                ? ""
                : book.getBookAuthors().get(0).getAuthorId();
        model.addAttribute("book", book);
        model.addAttribute("authors", catalogService.findAllAuthors());
        model.addAttribute("selectedAuthorId", selectedAuthorId);
        model.addAttribute("editMode", true);
        return "book/form";
    }

    @PostMapping("/{id}/edit")
    public String updateBook(
            @PathVariable String id,
            @ModelAttribute("book") BookDetails book,
            @RequestParam(required = false) String authorId) {
        book.setId(id);
        catalogService.saveBook(book);
        catalogService.updateBookPrimaryAuthor(id, authorId);
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            catalogService.deletePublication(id);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/books";
    }
}
