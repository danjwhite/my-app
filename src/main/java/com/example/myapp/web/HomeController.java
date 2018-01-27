package com.example.myapp.web;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import com.example.myapp.domain.User;
import com.example.myapp.service.ISecurityService;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ISecurityService securityService;

    @Autowired
    private IUserService userService;

    @ModelAttribute("user")
    public User user() {
        UserDetails principal = securityService.getPrincipal();
        return userService.findByUsername(principal.getUsername());
    }

    @RequestMapping(method = GET)
    public String home(@ModelAttribute("user") User user) {
        return "home";
    }
}