package com.example.talentlink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@RequestParam(name = "logout", required = false) String logout, Model model) {
        if (logout != null) {
            model.addAttribute("successMessage", "Bạn đã đăng xuất.");
        }
        return "index";
    }
}
