package com.example.myapp.web;

import lombok.Data;

import java.util.Map;

@Data
public class RestResponse {

    private boolean hasErrors;
    private Map<String, String> errors;
}
