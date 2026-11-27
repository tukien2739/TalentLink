package com.example.talentlink.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = RegistrationPasswordsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegistrationPasswords {

    String message() default "Mật khẩu không hợp lệ.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
