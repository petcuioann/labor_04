package com.example.library.controller;

import com.example.library.model.Library;
import com.example.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/libraries")
public class LibraryWebController {
    private final LibraryService libraryService;

    public LibraryWebController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public String listLibraries(Model model) {
        model.addAttribute("libraries", libraryService.findAllLibraries());
        return "library/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("library", new Library());
        return "library/form";
    }

    @PostMapping
    public String createLibrary(@ModelAttribute("library") Library library) {
        libraryService.saveLibrary(library);
        return "redirect:/libraries";
    }

    @PostMapping("/{id}/delete")
    public String deleteLibrary(@PathVariable String id) {
        libraryService.deleteLibrary(id);
        return "redirect:/libraries";
    }
}
