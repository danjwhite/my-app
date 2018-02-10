package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private IUserService userService;

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public String handleError404(Model model) {
        User user = userService.getLoggedInUser();
        model.addAttribute("user", user);
        model.addAttribute("errorTitle", "404: Resource Not Found");
        model.addAttribute("errorDescription", "The requested resource could not be found.");

        return "error";
    }
}
