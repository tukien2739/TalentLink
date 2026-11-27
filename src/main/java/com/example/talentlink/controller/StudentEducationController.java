package com.example.talentlink.controller;

import java.security.Principal;

import com.example.talentlink.dto.EducationForm;
import com.example.talentlink.service.StudentEducationService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/me/profile/educations")
public class StudentEducationController {

    private static final String FORM_VIEW = "student/education-form";
    private static final String PROFILE_REDIRECT = "redirect:/me/profile#education";

    private final StudentEducationService educations;

    public StudentEducationController(StudentEducationService educations) {
        this.educations = educations;
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("educationForm", new EducationForm(null, null, null, null, null));
        return FORM_VIEW;
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("educationForm") EducationForm form, BindingResult errors,
            Principal principal, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return FORM_VIEW;
        }
        educations.create(principal.getName(), form);
        redirect.addFlashAttribute("successMessage", "Đã thêm mục học vấn.");
        return PROFILE_REDIRECT;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable long id, Principal principal, Model model) {
        model.addAttribute("educationForm", educations.findOwn(principal.getName(), id).form());
        model.addAttribute("educationId", id);
        return FORM_VIEW;
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable long id, @Valid @ModelAttribute("educationForm") EducationForm form,
            BindingResult errors, Principal principal, Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            educations.findOwn(principal.getName(), id);
            model.addAttribute("educationId", id);
            return FORM_VIEW;
        }
        educations.update(principal.getName(), id, form);
        redirect.addFlashAttribute("successMessage", "Đã cập nhật mục học vấn.");
        return PROFILE_REDIRECT;
    }

    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable long id, Principal principal, Model model) {
        model.addAttribute("education", educations.findOwn(principal.getName(), id));
        return "student/confirm-delete-education";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable long id, Principal principal, RedirectAttributes redirect) {
        educations.delete(principal.getName(), id);
        redirect.addFlashAttribute("successMessage", "Đã xóa mục học vấn.");
        return PROFILE_REDIRECT;
    }
}
