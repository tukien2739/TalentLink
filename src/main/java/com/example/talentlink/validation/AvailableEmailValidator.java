package com.example.talentlink.validation;

import com.example.talentlink.service.StudentRegistrationService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AvailableEmailValidator implements ConstraintValidator<AvailableEmail, String> {

    private final StudentRegistrationService registration;

    public AvailableEmailValidator(StudentRegistrationService registration) {
        this.registration = registration;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email == null || email.isBlank() || email.length() > 254 || !registration.emailExists(email);
    }
}
