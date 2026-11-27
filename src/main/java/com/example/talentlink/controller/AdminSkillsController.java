package com.example.talentlink.controller;

import com.example.talentlink.dto.SkillForm;
import com.example.talentlink.dto.SkillForm.Category;
import com.example.talentlink.repository.SkillRepository;
import com.example.talentlink.repository.SkillRepository.Skill;

import jakarta.validation.Valid;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/skills")
public class AdminSkillsController {

    private final SkillRepository skills;

    public AdminSkillsController(SkillRepository skills) {
        this.skills = skills;
    }

    @ModelAttribute("categories")
    public Category[] categories() {
        return Category.values();
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("skills", skills.findAll());
        return "admin/skills";
    }

    @GetMapping("/new")
    public String newSkill(Model model) {
        model.addAttribute("skillForm", new SkillForm("", null));
        return "admin/skill-form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable long id, Model model) {
        Skill skill = skills.findById(id).orElseThrow(AdminSkillsController::notFound);
        model.addAttribute("skillId", id);
        model.addAttribute("skillForm", new SkillForm(skill.name(), skill.category()));
        return "admin/skill-form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("skillForm") SkillForm form, BindingResult errors,
            RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "admin/skill-form";
        }
        try {
            skills.create(form);
        } catch (DuplicateKeyException exception) {
            return duplicateName(errors);
        }
        redirect.addFlashAttribute("successMessage", "Đã thêm kỹ năng.");
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable long id, @Valid @ModelAttribute("skillForm") SkillForm form,
            BindingResult errors, Model model, RedirectAttributes redirect) {
        model.addAttribute("skillId", id);
        if (errors.hasErrors()) {
            return "admin/skill-form";
        }
        try {
            requireExisting(id, skills.update(id, form));
        } catch (DuplicateKeyException exception) {
            return duplicateName(errors);
        }
        redirect.addFlashAttribute("successMessage", "Đã cập nhật kỹ năng.");
        return "redirect:/admin/skills";
    }

    @PostMapping("/{id}/hide")
    public String hide(@PathVariable long id, RedirectAttributes redirect) {
        requireExisting(id, skills.hide(id));
        redirect.addFlashAttribute("successMessage", "Đã ẩn kỹ năng.");
        return "redirect:/admin/skills";
    }

    private void requireExisting(long id, int updated) {
        if (updated == 0 && skills.findById(id).isEmpty()) {
            throw notFound();
        }
    }

    private static String duplicateName(BindingResult errors) {
        errors.rejectValue("name", "Duplicate", "Tên kỹ năng đã tồn tại.");
        return "admin/skill-form";
    }

    private static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
