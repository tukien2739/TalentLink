package com.example.talentlink.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import com.example.talentlink.dto.PasswordForms.Reset;
import com.example.talentlink.dto.PasswordForms.ResetRequest;
import com.example.talentlink.service.AccountPasswordService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Profile("dev & otp-local & !prod & !production & !staging & !shared")
@ConditionalOnProperty(name = "server.address", havingValue = "127.0.0.1")
public class PasswordRecoveryController {

    private final AccountPasswordService passwords;

    private final Path mailbox;

    public PasswordRecoveryController(AccountPasswordService passwords,
            @Value("${talentlink.otp-local.directory}") Path mailbox) throws IOException {
        this.passwords = passwords;
        this.mailbox = mailbox.toAbsolutePath().normalize();
        if (!Files.isDirectory(this.mailbox, LinkOption.NOFOLLOW_LINKS)
                || !Files.getPosixFilePermissions(this.mailbox, LinkOption.NOFOLLOW_LINKS)
                        .equals(PosixFilePermissions.fromString("rwx------"))) {
            throw new IllegalStateException("Local OTP requires an existing private directory with mode 0700");
        }
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
        // Reserve a private file before issuance so a missing mailbox does not consume the cooldown.
        Path delivery = null;
        try {
            delivery = Files.createTempFile(mailbox, "reset-", ".otp",
                    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
            var code = passwords.issueResetCode(form.email());
            if (code.isPresent()) {
                Files.writeString(delivery, code.get());
                delivery = null;
            }
        } catch (IOException ignored) {
            // Keep the HTTP response identical across account and delivery outcomes.
        } finally {
            if (delivery != null) {
                try {
                    Files.deleteIfExists(delivery);
                } catch (IOException ignored) {
                    // Failed delivery files remain in the private local directory.
                }
            }
        }
        redirect.addFlashAttribute("passwordReset", new Reset(form.email(), null, null));
        redirect.addFlashAttribute("infoMessage", "Nếu tài khoản có thể đặt lại mật khẩu, mã sẽ được gửi vào thư mục OTP cục bộ.");
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
