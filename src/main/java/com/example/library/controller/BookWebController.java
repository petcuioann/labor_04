package com.example.library.controller;

import com.example.library.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
