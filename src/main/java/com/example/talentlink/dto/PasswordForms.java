package com.example.talentlink.dto;

import java.util.Locale;

import com.example.talentlink.validation.BcryptLength;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class PasswordForms {

    private PasswordForms() {}

    public record Change(
            @NotBlank(message = "Nhập mật khẩu hiện tại.") @BcryptLength String currentPassword,
            @NotBlank(message = "Nhập mật khẩu mới.")
            @Size(min = 8, message = "Mật khẩu mới cần ít nhất 8 ký tự.")
            @BcryptLength String newPassword) {

        @Override
        public String toString() {
            return "PasswordChange";
        }
    }

    public record ResetRequest(
            @NotBlank(message = "Nhập email.")
            @Email(message = "Nhập địa chỉ email hợp lệ.")
            @Size(max = 254, message = "Email tối đa 254 ký tự.") String email) {

        public ResetRequest {
            email = normalizeEmail(email);
        }
    }

    public record Reset(
            @NotBlank(message = "Nhập email.")
            @Email(message = "Nhập địa chỉ email hợp lệ.")
            @Size(max = 254, message = "Email tối đa 254 ký tự.") String email,
            @NotBlank(message = "Nhập mã OTP.")
            @Pattern(regexp = "[0-9]{6}", message = "Mã OTP gồm đúng 6 chữ số.") String code,
            @NotBlank(message = "Nhập mật khẩu mới.")
            @Size(min = 8, message = "Mật khẩu mới cần ít nhất 8 ký tự.")
            @BcryptLength String newPassword) {

        public Reset {
            email = normalizeEmail(email);
        }

        @Override
        public String toString() {
            return "PasswordReset";
        }
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }
}
