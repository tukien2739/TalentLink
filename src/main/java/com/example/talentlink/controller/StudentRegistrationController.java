package com.example.talentlink.controller;

import com.example.talentlink.dto.StudentRegistrationForm;
import com.example.talentlink.service.StudentRegistrationService;
import com.example.talentlink.service.StudentRegistrationService.EmailAlreadyUsedException;
import com.example.talentlink.validation.AvailableEmail;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentRegistrationController {

    private final StudentRegistrationService registration;

    public StudentRegistrationController(StudentRegistrationService registration) {
        this.registration = registration;
    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        model.addAttribute("registration", new StudentRegistrationForm("", "", null, null));
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registration") StudentRegistrationForm form,
            BindingResult errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "register";
        }
        try {
            registration.register(form);
        } catch (EmailAlreadyUsedException e) {
            errors.rejectValue("email", "AvailableEmail", AvailableEmail.MESSAGE);
            return "register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công. Bạn có thể đăng nhập ngay.");
        return "redirect:/login";
    }
}
