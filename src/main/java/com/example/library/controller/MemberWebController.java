package com.example.library.controller;

import com.example.library.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
