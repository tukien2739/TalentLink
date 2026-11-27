package com.example.talentlink.validation;

import com.example.talentlink.dto.StudentRegistrationForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegistrationPasswordsValidator
        implements ConstraintValidator<ValidRegistrationPasswords, StudentRegistrationForm> {

    @Override
    public boolean isValid(StudentRegistrationForm form, ConstraintValidatorContext context) {
        if (form == null || form.password() == null || form.password().isBlank()) {
            return true;
        }
        String confirmation = form.passwordConfirmation();
        if (confirmation == null || confirmation.isBlank() || form.password().equals(confirmation)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Mật khẩu nhập lại không khớp.")
                .addPropertyNode("passwordConfirmation").addConstraintViolation();
        return false;
    }
}
