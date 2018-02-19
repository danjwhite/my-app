package com.example.myapp.web;

import com.example.myapp.domain.Role;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.IRoleService;
import com.example.myapp.service.ISecurityService;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/register")
public class UserRegistrationController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private ISecurityService securityService;

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @ModelAttribute("allRoles")
    public List<Role> roles() {
        return roleService.findAll();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showRegistrationForm(Model model) {

        // Have standard user role in the select box selected by default.
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        Role role = roleService.findByType("ROLE_USER");

        userRegistrationDto.getRoles().add(role);
        model.addAttribute("user", userRegistrationDto);

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
        redirectAttributes.addAttribute("confirmation", "created");


        if (securityService.currentAuthenticationHasRole("ROLE_ADMIN")) {
            return "redirect:/admin";
        } else {
            securityService.autoLogin(username, userRegistrationDto.getPassword());

            return "redirect:/user/" + username + "/view";
        }
    }
}
