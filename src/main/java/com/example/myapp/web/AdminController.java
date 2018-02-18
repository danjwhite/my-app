package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/")
public class AdminController {

    private IUserService userService;

    @Autowired
    public AdminController(IUserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public User user() {
        return userService.getLoggedInUser();
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdminPage(@ModelAttribute("user") User user, Model model) {

        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        return "admin";
    }
}
