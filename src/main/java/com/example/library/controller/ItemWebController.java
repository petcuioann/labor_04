package com.example.library.controller;

import com.example.library.model.ReadableItem;
import com.example.library.service.CatalogService;
import com.example.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("item", new ReadableItem());
        model.addAttribute("publications", catalogService.findAllPublications());
        model.addAttribute("libraries", libraryService.findAllLibraries());
        return "item/form";
    }

    @PostMapping
    public String createItem(
            @ModelAttribute("item") ReadableItem item,
            @RequestParam String libraryId) {
        catalogService.addCopy(item, libraryId);
        return "redirect:/items";
    }
}
