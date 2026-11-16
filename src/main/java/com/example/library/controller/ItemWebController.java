package com.example.library.controller;

import com.example.library.model.ReadableItem;
import com.example.library.service.CatalogService;
import com.example.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("selectedLibraryId", "");
        model.addAttribute("statuses", ReadableItem.Status.values());
        model.addAttribute("editMode", false);
        return "item/form";
    }

    @PostMapping
    public String createItem(
            @ModelAttribute("item") ReadableItem item,
            @RequestParam String libraryId) {
        catalogService.addCopy(item, libraryId);
        return "redirect:/items";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable String id, Model model) {
        ReadableItem item = catalogService.findItem(id);
        if (item == null) {
            return "redirect:/items";
        }
        model.addAttribute("item", item);
        model.addAttribute("libraryId", catalogService.findLibraryIdForItem(id));
        return "item/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        ReadableItem item = catalogService.findItem(id);
        if (item == null) {
            return "redirect:/items";
        }
        model.addAttribute("item", item);
        model.addAttribute("publications", catalogService.findAllPublications());
        model.addAttribute("libraries", libraryService.findAllLibraries());
        model.addAttribute("selectedLibraryId", catalogService.findLibraryIdForItem(id));
        model.addAttribute("statuses", ReadableItem.Status.values());
        model.addAttribute("editMode", true);
        return "item/form";
    }

    @PostMapping("/{id}/edit")
    public String updateItem(
            @PathVariable String id,
            @ModelAttribute("item") ReadableItem item,
            @RequestParam String libraryId) {
        item.setId(id);
        catalogService.updateCopy(item, libraryId);
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            catalogService.deleteItem(id);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/items";
    }
}
