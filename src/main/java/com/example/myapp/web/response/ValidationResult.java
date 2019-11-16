package com.example.myapp.web.response;

import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

@Data
class ValidationResult {

    private final Map<String, String> errors;

    private ValidationResult(Map<String, String> errors) {
        this.errors = errors;
    }

    static ValidationResult of(BindingResult bindingResult) {
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

        return new ValidationResult(errors);
    }
}
