package com.example.myapp.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class ErrorController {

    @RequestMapping(value = "/request/error", method = RequestMethod.GET)
    public String getError(Model model) {
        model.addAttribute("errorTitle", "Controller method actually got called");
        return "error";
    }
}
