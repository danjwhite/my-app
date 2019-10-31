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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/register")
@SessionAttributes("allRoles")
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
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.getRoles().add(roleService.findByType(RoleType.ROLE_USER));

        return userRegistrationDto;
    }

    @ModelAttribute("allRoles")
    public List<Role> roles() {
        return roleService.findAll();
    }

    @GetMapping
    public String showRegistrationForm(Model model, HttpSession session) {

        if (!securityService.isCurrentAuthenticationAnonymous()) {
            session.setAttribute("userInContext", userService.getLoggedInUser());
        }

        return "registrationForm";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") @Valid UserRegistrationDto userRegistrationDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "registrationForm";
        }

        String username = userRegistrationDto.getUsername();
        if (userService.userExists(username)) {
            result.rejectValue("username", "UsernameAlreadyTaken", "There is already an account registered with this username");
            return "registrationForm";
        }

        userService.add(userRegistrationDto);
        redirectAttributes.addAttribute("confirmation", "created");

        String redirectUrl = "redirect:/user/" + username + "/view";
        if (securityService.currentAuthenticationHasRole(RoleType.ROLE_ADMIN)) {
            return redirectUrl;
        } else {
            securityService.autoLogin(username, userRegistrationDto.getPassword());
            return redirectUrl;
        }
    }
}
