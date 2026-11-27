package com.example.talentlink.controller;

import java.security.Principal;

import com.example.talentlink.dto.StudentProfileForm;
import com.example.talentlink.service.StudentEducationService;
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
    private final StudentEducationService educations;

    public StudentProfileController(StudentProfileService profiles, StudentEducationService educations) {
        this.profiles = profiles;
        this.educations = educations;
    }

    @GetMapping("/me/profile")
    public String edit(Principal principal, Model model) {
        model.addAttribute("profileForm", profiles.findOwnProfile(principal.getName()));
        return showProfile(principal, model);
    }

    @PostMapping("/me/profile")
    public String update(@Valid @ModelAttribute("profileForm") StudentProfileForm form, BindingResult errors,
            Principal principal, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return showProfile(principal, model);
        }
        profiles.updateOwnProfile(principal.getName(), form);
        redirect.addFlashAttribute("successMessage", "Đã lưu hồ sơ của bạn.");
        return "redirect:/me/profile";
    }

    private String showProfile(Principal principal, Model model) {
        model.addAttribute("educations", educations.listOwn(principal.getName()));
        return "student/profile";
    }
}
