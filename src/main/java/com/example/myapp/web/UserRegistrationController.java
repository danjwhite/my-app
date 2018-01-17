package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.ISecurityService;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class UserRegistrationController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityService securityService;

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());

        return "registrationForm";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") @Valid UserRegistrationDto userRegistrationDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        User existing = userService.findByUsername(userRegistrationDto().getUsername());
        if (existing != null) {
            result.rejectValue("username","There is already an account registered with this username");
        }

        if (result.hasErrors()) {
            return "registrationForm";
        }

        Long userId = userService.add(userRegistrationDto).getId();

        securityService.autoLogin(userRegistrationDto.getUsername(), userRegistrationDto.getPassword());

        redirectAttributes.addAttribute("userId", userId);
        redirectAttributes.addAttribute("confirmation", "created");

        return "redirect:/account/view";
    }
}
