package com.example.myapp.web;

import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.ISecurityService;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
        String username = userRegistrationDto.getUsername();


        if (userService.userExists(username)) {
            result.rejectValue("username", null, "There is already an account registered with this username");
        }

        if (result.hasErrors()) {
            return "registrationForm";
        }

        userService.add(userRegistrationDto);
        securityService.autoLogin(username, userRegistrationDto.getPassword());

        redirectAttributes.addAttribute("confirmation", "created");

        return "redirect:/user/" + username + "/view";
    }
}
