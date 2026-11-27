package com.example.talentlink.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StudentSkillForm(
        @NotNull(message = "Chọn kỹ năng từ danh mục.") @Min(1) Long skillId,
        @NotNull(message = "Chọn mức kỹ năng.")
        @Min(value = 1, message = "Mức kỹ năng phải từ 1 đến 5.")
        @Max(value = 5, message = "Mức kỹ năng phải từ 1 đến 5.") Integer level) {
}
