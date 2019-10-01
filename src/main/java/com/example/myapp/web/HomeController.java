package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

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