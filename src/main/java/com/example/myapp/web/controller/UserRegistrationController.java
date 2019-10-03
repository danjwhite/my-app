package com.example.myapp.web.controller;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.RoleService;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/register")
public class UserRegistrationController {

    private final UserService userService;
    private final RoleService roleService;
    private final SecurityService securityService;

    public UserRegistrationController(UserService userService, RoleService roleService, SecurityService securityService) {
        this.userService = userService;
        this.roleService = roleService;
        this.securityService = securityService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @ModelAttribute("allRoles")
    public List<Role> roles() {
        return roleService.findAll();
    }

    @GetMapping
    public String showRegistrationForm(Model model) {

        // Have standard user role in the select box selected by default.
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        Role role = roleService.findByType(RoleType.ROLE_USER);

        userRegistrationDto.getRoles().add(role);
        model.addAttribute("user", userRegistrationDto);

        return "registrationForm";
    }

    @PostMapping
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
        redirectAttributes.addAttribute("confirmation", "created");


        if (securityService.currentAuthenticationHasRole(RoleType.ROLE_ADMIN)) {
            return "redirect:/admin";
        } else {
            securityService.autoLogin(username, userRegistrationDto.getPassword());

            return "redirect:/user/" + username + "/view";
        }
    }
}
