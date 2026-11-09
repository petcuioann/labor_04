package com.example.library.controller;

import com.example.library.model.ReadableItem;
import com.example.library.model.Reservation;
import com.example.library.service.CatalogService;
import com.example.library.service.CirculationService;
import com.example.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("members", libraryService.findAllMembers());
        model.addAttribute("availableItems", catalogService.findItemsByStatus(ReadableItem.Status.AVAILABLE));
        return "reservation/form";
    }

    @PostMapping
    public String createReservation(@ModelAttribute("reservation") Reservation reservation) {
        circulationService.reserve(reservation.getMemberId(), reservation.getReadableItemId());
        return "redirect:/reservations";
    }

    @PostMapping("/{id}/delete")
    public String deleteReservation(@PathVariable String id) {
        circulationService.deleteReservation(id);
        return "redirect:/reservations";
    }
}
