package com.example.talentlink.dto;

import java.util.Locale;

import com.example.talentlink.validation.AvailableEmail;
import com.example.talentlink.validation.BcryptLength;
import com.example.talentlink.validation.ValidRegistrationPasswords;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@ValidRegistrationPasswords
public record StudentRegistrationForm(
        @NotBlank(message = "Nhập họ và tên.")
        @Size(max = 150, message = "Họ và tên tối đa 150 ký tự.")
        String fullName,

        @NotBlank(message = "Nhập email.")
        @Email(message = "Nhập địa chỉ email hợp lệ.")
        @Size(max = 254, message = "Email tối đa 254 ký tự.")
        @AvailableEmail
        String email,

        @NotBlank(message = "Nhập mật khẩu.")
        @Size(min = 8, message = "Mật khẩu cần ít nhất 8 ký tự.")
        @BcryptLength
        String password,

        @NotBlank(message = "Nhập lại mật khẩu.")
        String passwordConfirmation) {

    public StudentRegistrationForm {
        fullName = fullName == null ? null : fullName.strip();
        email = email == null ? null : email.strip().toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return "StudentRegistrationForm";
    }
}
