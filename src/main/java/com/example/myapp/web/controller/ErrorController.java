package com.example.myapp.web.controller;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@SessionAttributes("userInContext")
@RequestMapping("/")
public class ErrorController {

    private final UserService userService;

    public ErrorController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("userInContext")
    public UserDTO userInContext() {
        return userService.getUserInContext();
    }

    @GetMapping(value = "/error/{code}")
    public String getError(@PathVariable("code") String code, @ModelAttribute("user") User user, Model model) {

        switch (code) {
            case "403":
                model.addAttribute("errorTitle", "403: Forbidden");
                model.addAttribute("errorDescription", "The request could not be completed.");
                break;
            case "404":
                model.addAttribute("errorTitle", "404: Resource Not Found");
                model.addAttribute("errorDescription", "The request could not be completed.");
                break;
        }

        return "error";
    }
}
