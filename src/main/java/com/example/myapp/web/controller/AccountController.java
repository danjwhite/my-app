package com.example.myapp.web.controller;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("userInContext")
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("userInContext")
    public UserDTO userInContext() {
        return userService.getUserInContext();
    }

    @GetMapping
    public String getAccountPage() {
        return "account";
    }
}
