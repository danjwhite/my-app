package com.example.myapp.web.controller;

import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("userInContext")
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("userInContext")
    public User user() {
        return userService.getLoggedInUser();
    }

    @ModelAttribute("roles")
    public List<RoleType> roles() {
        return Arrays.stream(RoleType.values()).collect(Collectors.toList());
    }

    @ModelAttribute("defaultRole")
    public RoleType defaultRole() {
        return RoleType.ROLE_USER;
    }

    @GetMapping
    public String getAdminPage() {
        return "admin";
    }
}
