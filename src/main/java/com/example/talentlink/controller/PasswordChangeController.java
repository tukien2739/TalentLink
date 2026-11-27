package com.example.talentlink.controller;

import com.example.talentlink.dto.PasswordForms.Change;
import com.example.talentlink.service.AccountPasswordService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordChangeController {

    private final AccountPasswordService passwords;
    private final SecurityContextLogoutHandler sessionLogout = new SecurityContextLogoutHandler();
    private final CookieClearingLogoutHandler rememberMeLogout = new CookieClearingLogoutHandler("remember-me");

    public PasswordChangeController(AccountPasswordService passwords) {
        this.passwords = passwords;
    }

    @GetMapping("/account/password")
    public String changeForm(Model model) {
        model.addAttribute("passwordChange", new Change(null, null));
        return "change-password";
    }

    @PostMapping("/account/password")
    public String change(@Valid @ModelAttribute("passwordChange") Change form, BindingResult errors,
            Authentication authentication, HttpServletRequest request, HttpServletResponse response,
            RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "change-password";
        }
        if (!passwords.change(authentication.getName(), form.currentPassword(), form.newPassword())) {
            errors.rejectValue("currentPassword", "invalid", "Mật khẩu hiện tại không đúng hoặc tài khoản không khả dụng.");
            return "change-password";
        }
        sessionLogout.logout(request, response, authentication);
        rememberMeLogout.logout(request, response, authentication);
        redirect.addFlashAttribute("successMessage", "Đã đổi mật khẩu. Hãy đăng nhập lại.");
        return "redirect:/login";
    }
}
