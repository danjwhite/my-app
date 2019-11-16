package com.example.myapp.web.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Objects;

public class ResponseFactory {

    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    public static ResponseEntity<Void> noContent() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity<ValidationResult> badRequest(BindingResult bindingResult) {
        Objects.requireNonNull(bindingResult, "bindingResult cannot be null.");
        return new ResponseEntity<>(ValidationResult.of(bindingResult), HttpStatus.BAD_REQUEST);
    }
}
