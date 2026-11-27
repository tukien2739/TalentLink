package com.example.talentlink.dto;

import com.example.talentlink.validation.ProfileBirthDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StudentProfileForm(
        @NotBlank(message = "Nhập họ và tên.")
        @Size(max = 150, message = "Họ và tên tối đa 150 ký tự.") String fullName,
        @ProfileBirthDate String birthDate,
        @Pattern(regexp = "(?:0[0-9]{9}|\\+[1-9][0-9]{7,14})",
                message = "Nhập số Việt Nam gồm 10 chữ số bắt đầu bằng 0 hoặc số quốc tế có dấu +.") String phone,
        @Size(max = 16000, message = "Giới thiệu tối đa 16.000 ký tự.") String summary,
        @Size(max = 150, message = "Vị trí mong muốn tối đa 150 ký tự.") String desiredPosition,
        @Size(max = 150, message = "Nơi làm việc mong muốn tối đa 150 ký tự.") String preferredLocation) {

    public StudentProfileForm {
        fullName = stripToNull(fullName);
        birthDate = stripToNull(birthDate);
        phone = stripToNull(phone);
        summary = stripToNull(summary);
        desiredPosition = stripToNull(desiredPosition);
        preferredLocation = stripToNull(preferredLocation);
    }

    private static String stripToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
