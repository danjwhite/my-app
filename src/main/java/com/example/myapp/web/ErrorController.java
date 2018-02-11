package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class ErrorController {

    @Autowired
    private IUserService userService;

    @ModelAttribute("user")
    public User user() {
        return userService.getLoggedInUser();
    }

    @RequestMapping(value = "/error/{code}", method = RequestMethod.GET)
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
