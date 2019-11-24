package com.example.myapp.web.controller;

import com.example.myapp.dto.UserDTO;
import com.example.myapp.service.UserInContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/")
@SessionAttributes("userInContext")
@RequiredArgsConstructor
public class HomeController {

    private final UserInContextService userInContextService;

    @ModelAttribute("userInContext")
    public UserDTO userInContext() {
        return userInContextService.getUserInContext();
    }

    @GetMapping
    public String getHomePage() {
        return "home";
    }
}