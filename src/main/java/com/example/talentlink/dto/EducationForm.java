package com.example.talentlink.dto;

import com.example.talentlink.validation.EducationDateRange;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@EducationDateRange
public record EducationForm(
        @NotBlank(message = "Nhập tên trường.")
        @Size(max = 200, message = "Tên trường tối đa 200 ký tự.") String institution,
        @NotBlank(message = "Nhập chuyên ngành.")
        @Size(max = 150, message = "Chuyên ngành tối đa 150 ký tự.") String fieldOfStudy,
        @NotBlank(message = "Nhập ngày bắt đầu.") String startDate,
        String endDate,
        @Pattern(regexp = "(?:[0-3](?:[.,][0-9]{1,4})?|4(?:[.,]0{1,4})?)",
                message = "GPA phải từ 0 đến 4, với tối đa 4 chữ số thập phân.") String gpa) {

    public EducationForm {
        institution = stripToNull(institution);
        fieldOfStudy = stripToNull(fieldOfStudy);
        startDate = stripToNull(startDate);
        endDate = stripToNull(endDate);
        gpa = stripToNull(gpa);
    }

    private static String stripToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
