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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("editMode", false);
        return "library/form";
    }

    @PostMapping
    public String createLibrary(@ModelAttribute("library") Library library) {
        libraryService.saveLibrary(library);
        return "redirect:/libraries";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable String id, Model model) {
        Library library = libraryService.findLibrary(id);
        if (library == null) {
            return "redirect:/libraries";
        }
        model.addAttribute("library", library);
        return "library/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Library library = libraryService.findLibrary(id);
        if (library == null) {
            return "redirect:/libraries";
        }
        model.addAttribute("library", library);
        model.addAttribute("editMode", true);
        return "library/form";
    }

    @PostMapping("/{id}/edit")
    public String updateLibrary(@PathVariable String id, @ModelAttribute("library") Library library) {
        library.setId(id);
        libraryService.saveLibrary(library);
        return "redirect:/libraries";
    }

    @PostMapping("/{id}/delete")
    public String deleteLibrary(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.deleteLibrary(id);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/libraries";
    }
}
