package com.example.talentlink.controller;

import java.security.Principal;

import com.example.talentlink.dto.StudentProjectForm;
import com.example.talentlink.repository.StudentProjectRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/me/profile/projects")
public class StudentProjectController {

    private static final String LIST = "redirect:/me/profile/projects";
    private static final String VIEW = "student/projects";
    private final StudentProjectRepository projects;

    public StudentProjectController(StudentProjectRepository projects) {
        this.projects = projects;
    }

    @GetMapping
    public String list(Principal principal, Model model) {
        model.addAttribute("projectForm", new StudentProjectForm(null, null, null, null, null, null));
        return page(principal, model);
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable long id, Principal principal, Model model) {
        var project = projects.findOwn(principal.getName(), id).orElseThrow(this::notFound);
        model.addAttribute("projectId", id);
        model.addAttribute("projectForm", project.form());
        return page(principal, model);
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("projectForm") StudentProjectForm form, BindingResult errors,
            Principal principal, Model model, RedirectAttributes redirect) {
        validateLinks(form, errors);
        if (!errors.hasErrors()) {
            if (projects.add(principal.getName(), form)) {
                redirect.addFlashAttribute("successMessage", "Đã thêm dự án.");
                return LIST;
            }
            errors.reject("projectLimit", "Mỗi hồ sơ chỉ được có tối đa 20 dự án.");
        }
        return page(principal, model);
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable long id, @Valid @ModelAttribute("projectForm") StudentProjectForm form,
            BindingResult errors, Principal principal, Model model, RedirectAttributes redirect) {
        projects.findOwn(principal.getName(), id).orElseThrow(this::notFound);
        validateLinks(form, errors);
        if (errors.hasErrors()) {
            model.addAttribute("projectId", id);
            return page(principal, model);
        }
        if (projects.update(principal.getName(), id, form) == 0) {
            throw notFound();
        }
        redirect.addFlashAttribute("successMessage", "Đã lưu dự án.");
        return LIST;
    }

    @PostMapping("/{id}/remove")
    public String remove(@PathVariable long id, Principal principal, RedirectAttributes redirect) {
        if (projects.remove(principal.getName(), id) == 0) {
            throw notFound();
        }
        redirect.addFlashAttribute("successMessage", "Đã xoá dự án.");
        return LIST;
    }

    private String page(Principal principal, Model model) {
        model.addAttribute("projects", projects.findOwn(principal.getName()));
        return VIEW;
    }

    private void validateLinks(StudentProjectForm form, BindingResult errors) {
        if (!errors.hasFieldErrors("githubUrl") && !StudentProjectForm.validUrl(form.githubUrl(), true)) {
            errors.rejectValue("githubUrl", "invalidUrl", "Nhập URL GitHub hợp lệ (http hoặc https).");
        }
        if (form.demoUrl() != null && !form.demoUrl().isBlank() && !errors.hasFieldErrors("demoUrl")
                && !StudentProjectForm.validUrl(form.demoUrl(), false)) {
            errors.rejectValue("demoUrl", "invalidUrl", "Nhập URL demo hợp lệ (http hoặc https).");
        }
    }

    private ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
