package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class UserRegistrationController {

    @Autowired
    private IUserService userService;

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showRegistrationForm() {
        return "register";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") @Valid UserRegistrationDto userRegistrationDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        User existing = userService.findByUsername(userRegistrationDto().getUserName());
        if (existing != null) {
            result.rejectValue("username", null, "There is already an account registered with this username");
        }

        if (result.hasErrors()) {
            return "register";
        }

        Long userId = userService.add(userRegistrationDto).getId();
        redirectAttributes.addAttribute("userId", userId);
        redirectAttributes.addAttribute("confirmation", "created");

        return "redirect:/account/view";
    }
}
