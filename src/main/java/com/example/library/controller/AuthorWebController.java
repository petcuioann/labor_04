package com.example.library.controller;

import com.example.library.model.Author;
import com.example.library.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorWebController {
    private final CatalogService catalogService;

    public AuthorWebController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", catalogService.findAllAuthors());
        return "author/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("author", new Author());
        return "author/form";
    }

    @PostMapping
    public String createAuthor(@ModelAttribute("author") Author author) {
        catalogService.saveAuthor(author);
        return "redirect:/authors";
    }
}
