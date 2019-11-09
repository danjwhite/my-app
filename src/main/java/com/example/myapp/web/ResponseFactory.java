package com.example.myapp.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

// TODO: Add tests
public class ResponseFactory {

    private ResponseFactory() {

    }

    public static ResponseEntity<RestResponse> error(BindingResult bindingResult) {
        RestResponse restResponse = new RestResponse();
        restResponse.setHasErrors(true);
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        restResponse.setErrors(errors);

        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    public static ResponseEntity<RestResponse> success(HttpStatus httpStatus) {
        RestResponse restResponse = new RestResponse();
        restResponse.setHasErrors(false);

        return new ResponseEntity<>(restResponse, httpStatus);
    }
}
