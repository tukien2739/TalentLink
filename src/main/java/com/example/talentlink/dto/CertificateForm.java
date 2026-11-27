package com.example.talentlink.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

public record CertificateForm(
        @NotBlank(message = "Nhập tên chứng chỉ.") @Size(max = 200) String name,
        @NotBlank(message = "Nhập đơn vị cấp.") @Size(max = 200) String issuer,
        @NotNull(message = "Nhập ngày cấp.") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issuedOn,
        MultipartFile evidence) {
}
