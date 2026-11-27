package com.example.talentlink.validation;

import java.nio.charset.StandardCharsets;

import com.example.talentlink.dto.StudentRegistrationForm;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegistrationPasswordsValidator
        implements ConstraintValidator<ValidRegistrationPasswords, StudentRegistrationForm> {

    private static final int BCRYPT_MAX_BYTES = 72;

    @Override
    public boolean isValid(StudentRegistrationForm form, ConstraintValidatorContext context) {
        if (form == null || form.password() == null || form.password().isBlank()) {
            return true;
        }
        String confirmation = form.passwordConfirmation();
        boolean withinLimit = form.password().getBytes(StandardCharsets.UTF_8).length <= BCRYPT_MAX_BYTES;
        boolean matches = confirmation == null || confirmation.isBlank() || form.password().equals(confirmation);
        if (withinLimit && matches) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        if (!withinLimit) {
            context.buildConstraintViolationWithTemplate("Mật khẩu tối đa 72 byte UTF-8.")
                    .addPropertyNode("password").addConstraintViolation();
        }
        if (!matches) {
            context.buildConstraintViolationWithTemplate("Mật khẩu nhập lại không khớp.")
                    .addPropertyNode("passwordConfirmation").addConstraintViolation();
        }
        return false;
    }
}
