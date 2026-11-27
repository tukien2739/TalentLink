package com.example.talentlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SkillForm(
        @NotBlank(message = "Nhập tên kỹ năng.")
        @Size(max = 100, message = "Tên kỹ năng tối đa 100 ký tự.") String name,
        @NotNull(message = "Chọn nhóm kỹ năng.") Category category) {

    public SkillForm {
        name = name == null ? null : name.strip();
    }

    public enum Category {
        LANGUAGE("Ngôn ngữ"),
        FRAMEWORK("Framework"),
        DATABASE("CSDL"),
        TOOL("Công cụ"),
        SOFT_SKILL("Kỹ năng mềm");

        private final String label;

        Category(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }
}
