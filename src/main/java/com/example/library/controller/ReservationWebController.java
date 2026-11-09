package com.example.library.controller;

import com.example.library.service.CatalogService;
import com.example.library.service.CirculationService;
import com.example.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reservations")
public class ReservationWebController {
    private final CirculationService circulationService;
    private final LibraryService libraryService;
    private final CatalogService catalogService;

    public ReservationWebController(
            CirculationService circulationService,
            LibraryService libraryService,
            CatalogService catalogService) {
        this.circulationService = circulationService;
        this.libraryService = libraryService;
        this.catalogService = catalogService;
    }

    @GetMapping
    public String listReservations(Model model) {
        model.addAttribute("reservations", circulationService.findAllReservations());
        return "reservation/index";
    }
}
