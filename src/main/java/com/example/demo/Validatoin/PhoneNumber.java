package com.example.demo.Validatoin;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Номер телефона должен быть в формате +70000000000 или 80000000000";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
