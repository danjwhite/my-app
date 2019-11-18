package com.example.myapp.web.controller;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("userInContext")
@RequestMapping("/")
public class HomeController {

    private final SecurityService securityService;
    private final UserService userService;

    public HomeController(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @ModelAttribute("userInContext")
    public UserDTO userInContext() {
        return userService.getUserInContext();
    }

    @GetMapping
    public String home(@ModelAttribute("user") User user) {
        return "home";
    }
}