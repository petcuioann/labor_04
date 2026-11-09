package com.example.library.controller;

import com.example.library.service.CatalogService;
import com.example.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/items")
public class ItemWebController {
    private final CatalogService catalogService;
    private final LibraryService libraryService;

    public ItemWebController(CatalogService catalogService, LibraryService libraryService) {
        this.catalogService = catalogService;
        this.libraryService = libraryService;
    }

    @GetMapping
    public String listItems(Model model) {
        model.addAttribute("items", catalogService.findAllItems());
        return "item/index";
    }
}
