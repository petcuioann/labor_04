package com.example.library.controller;

import com.example.library.service.CatalogService;
import com.example.library.service.CirculationService;
import com.example.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeWebController {
    private final LibraryService libraryService;
    private final CatalogService catalogService;
    private final CirculationService circulationService;

    public HomeWebController(
            LibraryService libraryService,
            CatalogService catalogService,
            CirculationService circulationService) {
        this.libraryService = libraryService;
        this.catalogService = catalogService;
        this.circulationService = circulationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("libraryCount", libraryService.findAllLibraries().size());
        model.addAttribute("authorCount", catalogService.findAllAuthors().size());
        model.addAttribute("bookCount", catalogService.findAllBooks().size());
        model.addAttribute("itemCount", catalogService.findAllItems().size());
        model.addAttribute("memberCount", libraryService.findAllMembers().size());
        model.addAttribute("loanCount", circulationService.findAllLoans().size());
        model.addAttribute("reservationCount", circulationService.findAllReservations().size());
        return "index";
    }
}
