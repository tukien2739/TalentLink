package com.example.talentlink.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping(value = "/", params = "logout")
    public String afterLogout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", "Bạn đã đăng xuất.");
        return "redirect:/";
    }
}
