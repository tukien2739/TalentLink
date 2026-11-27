package com.example.talentlink.controller;

import java.security.Principal;

import com.example.talentlink.dto.StudentProfileForm;
import com.example.talentlink.service.StudentProfileService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentProfileController {

    private final StudentProfileService profiles;

    public StudentProfileController(StudentProfileService profiles) {
        this.profiles = profiles;
    }

    @GetMapping("/me/profile")
    public String edit(Principal principal, Model model) {
        model.addAttribute("profileForm", profiles.findOwnProfile(principal.getName()));
        return "student/profile";
    }

    @PostMapping("/me/profile")
    public String update(@Valid @ModelAttribute("profileForm") StudentProfileForm form, BindingResult errors,
            Principal principal, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "student/profile";
        }
        profiles.updateOwnProfile(principal.getName(), form);
        redirect.addFlashAttribute("successMessage", "Đã lưu hồ sơ của bạn.");
        return "redirect:/me/profile";
    }
}
