package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "account/view", method = RequestMethod.GET)
    public String getUserAccount(@RequestParam(value = "userId") long userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);

        return "user";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "loginForm";
    }
}