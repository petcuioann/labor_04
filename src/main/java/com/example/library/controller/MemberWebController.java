package com.example.library.controller;

import com.example.library.model.Member;
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
@RequestMapping("/members")
public class MemberWebController {
    private final LibraryService libraryService;

    public MemberWebController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public String listMembers(Model model) {
        model.addAttribute("members", libraryService.findAllMembers());
        return "member/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("libraries", libraryService.findAllLibraries());
        model.addAttribute("editMode", false);
        return "member/form";
    }

    @PostMapping
    public String createMember(@ModelAttribute("member") Member member) {
        libraryService.registerMember(member);
        return "redirect:/members";
    }

    @GetMapping("/{id}")
    public String showDetails(@PathVariable String id, Model model) {
        Member member = libraryService.findMember(id);
        if (member == null) {
            return "redirect:/members";
        }
        model.addAttribute("member", member);
        return "member/details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        Member member = libraryService.findMember(id);
        if (member == null) {
            return "redirect:/members";
        }
        model.addAttribute("member", member);
        model.addAttribute("libraries", libraryService.findAllLibraries());
        model.addAttribute("editMode", true);
        return "member/form";
    }

    @PostMapping("/{id}/edit")
    public String updateMember(@PathVariable String id, @ModelAttribute("member") Member member) {
        member.setId(id);
        libraryService.registerMember(member);
        return "redirect:/members";
    }

    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.deleteMember(id);
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/members";
    }
}
