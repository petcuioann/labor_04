package com.example.library.controller;

import com.example.library.model.BookDetails;
import com.example.library.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
