package com.example.talentlink.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.example.talentlink.dto.EducationForm;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Constraint(validatedBy = EducationDateRange.Validator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EducationDateRange {

    String message() default "Ngày học không hợp lệ.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<EducationDateRange, EducationForm> {

        @Override
        public boolean isValid(EducationForm form, ConstraintValidatorContext context) {
            if (form == null) {
                return true;
            }
            LocalDate start = parse(form.startDate());
            LocalDate end = parse(form.endDate());
            boolean invalidStart = form.startDate() != null && start == null;
            boolean invalidEnd = form.endDate() != null && end == null;
            boolean reversed = start != null && end != null && start.isAfter(end);
            if (!invalidStart && !invalidEnd && !reversed) {
                return true;
            }

            context.disableDefaultConstraintViolation();
            if (invalidStart) {
                context.buildConstraintViolationWithTemplate("Ngày bắt đầu phải là ngày hợp lệ theo định dạng YYYY-MM-DD.")
                        .addPropertyNode("startDate").addConstraintViolation();
            }
            if (invalidEnd) {
                context.buildConstraintViolationWithTemplate("Ngày kết thúc phải là ngày hợp lệ theo định dạng YYYY-MM-DD.")
                        .addPropertyNode("endDate").addConstraintViolation();
            }
            if (reversed) {
                context.buildConstraintViolationWithTemplate("Ngày kết thúc phải bằng hoặc sau ngày bắt đầu.")
                        .addPropertyNode("endDate").addConstraintViolation();
            }
            return false;
        }

        private LocalDate parse(String value) {
            if (value == null || value.length() != 10) {
                return null;
            }
            try {
                LocalDate date = LocalDate.parse(value);
                return date.getYear() >= 1000 ? date : null;
            } catch (DateTimeParseException e) {
                return null;
            }
        }
    }
}
