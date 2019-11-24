package com.example.myapp.web.controller;

import com.example.myapp.domain.RoleType;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.service.UserInContextService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AdminController {

    private final UserInContextService userInContextService;

    @ModelAttribute("userInContext")
    public UserDTO userInContext() {
        return userInContextService.getUserInContext();
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
