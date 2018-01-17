package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/account")
public class UserController {

    @Autowired
    private IUserService userService;

    @RequestMapping("/view")
    public String getUserAccount(@RequestParam(value = "userId") long userId,
                                 @RequestParam(value = "confirmation", required = false) String confirmation,
                                 Model model) {
        User user = userService.findById(userId);
        model.addAttribute("user", user);

        return "user";
    }
}
