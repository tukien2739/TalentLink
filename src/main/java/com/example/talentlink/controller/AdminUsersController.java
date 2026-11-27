package com.example.talentlink.controller;

import java.security.Principal;

import com.example.talentlink.dto.PartnerAccountForm;
import com.example.talentlink.service.AdminAccountService;
import com.example.talentlink.service.AdminAccountService.PartnerEmailAlreadyUsedException;
import com.example.talentlink.service.AdminAccountService.Role;
import com.example.talentlink.service.AdminAccountService.State;
import com.example.talentlink.validation.AvailableEmail;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUsersController {

    private final AdminAccountService accounts;

    public AdminUsersController(AdminAccountService accounts) {
        this.accounts = accounts;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q, @RequestParam(required = false) Role role,
            @RequestParam(required = false) State state, @RequestParam(defaultValue = "1") long page,
            Principal principal, Model model) {
        String query = q.strip();
        model.addAttribute("accounts", accounts.search(query, role, state, page));
        model.addAttribute("query", query);
        model.addAttribute("roles", Role.values());
        model.addAttribute("states", State.values());
        model.addAttribute("filterRole", role);
        model.addAttribute("filterState", state);
        model.addAttribute("currentEmail", principal.getName());
        return "admin/users";
    }

    @GetMapping("/new")
    public String partnerForm(Model model) {
        model.addAttribute("partnerAccount", new PartnerAccountForm("", "", "", null));
        return "admin/new-partner";
    }

    @PostMapping("/new")
    public String createPartner(@Valid @ModelAttribute("partnerAccount") PartnerAccountForm form, BindingResult errors,
            RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "admin/new-partner";
        }
        try {
            accounts.createPartner(form);
        } catch (PartnerEmailAlreadyUsedException e) {
            errors.rejectValue("email", "AvailableEmail", AvailableEmail.MESSAGE);
            return "admin/new-partner";
        }
        redirect.addFlashAttribute("successMessage", "Đã tạo tài khoản đối tác.");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/status")
    public String confirmStatus(@PathVariable long id, @RequestParam boolean locked, Principal principal, Model model) {
        model.addAttribute("target", accounts.confirmStatus(id, locked, principal.getName()));
        model.addAttribute("locked", locked);
        return "admin/confirm-account-status";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable long id, @RequestParam boolean locked, Principal principal,
            RedirectAttributes redirect) {
        accounts.setLocked(id, locked, principal.getName());
        redirect.addFlashAttribute("successMessage", locked ? "Đã khóa tài khoản." : "Đã mở khóa tài khoản.");
        return "redirect:/admin/users";
    }
}
