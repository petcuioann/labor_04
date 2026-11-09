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
        return "member/form";
    }

    @PostMapping
    public String createMember(@ModelAttribute("member") Member member) {
        libraryService.registerMember(member);
        return "redirect:/members";
    }

    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable String id) {
        libraryService.deleteMember(id);
        return "redirect:/members";
    }
}
