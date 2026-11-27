package com.example.talentlink.controller;

import com.example.talentlink.dto.PasswordForms.Reset;
import com.example.talentlink.dto.PasswordForms.ResetRequest;
import com.example.talentlink.service.AccountPasswordService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("dev")
public class PasswordRecoveryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryController.class);

    private final AccountPasswordService passwords;

    public PasswordRecoveryController(AccountPasswordService passwords) {
        this.passwords = passwords;
    }

    @GetMapping("/forgot-password")
    public String requestForm(Model model) {
        model.addAttribute("resetRequest", new ResetRequest(""));
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String requestCode(@Valid @ModelAttribute("resetRequest") ResetRequest form, BindingResult errors,
            RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "forgot-password";
        }
        passwords.issueResetCode(form.email())
                .ifPresent(code -> LOGGER.info("Dev password reset OTP for {}: {}", form.email(), code));
        redirect.addFlashAttribute("passwordReset", new Reset(form.email(), null, null));
        redirect.addFlashAttribute("infoMessage", "Nếu tài khoản có thể đặt lại mật khẩu, hãy xem mã OTP trong log dev.");
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetForm(Model model) {
        if (!model.containsAttribute("passwordReset")) {
            model.addAttribute("passwordReset", new Reset("", null, null));
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String reset(@Valid @ModelAttribute("passwordReset") Reset form, BindingResult errors,
            RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            return "reset-password";
        }
        if (!passwords.reset(form.email(), form.code(), form.newPassword())) {
            errors.rejectValue("code", "invalid", "Mã OTP không còn hợp lệ. Kiểm tra mã hoặc yêu cầu mã mới.");
            return "reset-password";
        }
        redirect.addFlashAttribute("successMessage", "Đã đặt lại mật khẩu. Hãy đăng nhập bằng mật khẩu mới.");
        return "redirect:/login";
    }
}
