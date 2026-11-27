package com.example.talentlink.dto;

import java.net.URI;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentProjectForm(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 200) String projectRole,
        @NotBlank @Size(max = 500) String technologies,
        @NotBlank @Size(max = 2048) String githubUrl,
        @Size(max = 2048) String demoUrl,
        @NotBlank @Size(max = 16000) String description) {

    public StudentProjectForm {
        name = trim(name);
        projectRole = trim(projectRole);
        technologies = trim(technologies);
        githubUrl = trim(githubUrl);
        demoUrl = trim(demoUrl);
        description = trim(description);
    }

    public static boolean validUrl(String value, boolean github) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            URI uri = URI.create(value);
            return ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null && uri.getUserInfo() == null
                    && (!github || "github.com".equalsIgnoreCase(uri.getHost()));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.strip();
    }
}
