package com.example.talentlink.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Constraint(validatedBy = ProfileBirthDate.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfileBirthDate {

    String message() default "Ngày sinh phải đúng định dạng YYYY-MM-DD, có thật và không ở tương lai.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ProfileBirthDate, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            if (value.length() != 10) {
                return false;
            }
            try {
                LocalDate date = LocalDate.parse(value);
                return date.getYear() >= 1000 && !date.isAfter(LocalDate.now());
            } catch (DateTimeParseException e) {
                return false;
            }
        }
    }
}
