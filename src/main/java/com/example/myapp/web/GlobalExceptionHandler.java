package com.example.myapp.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ModelAndView handleError404() {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorTitle", "404: Resource Not Found");
        modelAndView.addObject("errorDescription", "The requested resource could not be found.");

        return modelAndView;
    }
}
