package com.example.demo.Validatoin;

import com.example.demo.Validatoin.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public void initialize(PhoneNumber phoneNumber) {
    }

    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext cxt) {
        return phoneField != null && (phoneField.matches("\\+7\\d{10}") || phoneField.matches("8\\d{10}"));
    }
}
