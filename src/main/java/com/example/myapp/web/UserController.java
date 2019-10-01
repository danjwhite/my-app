package com.example.myapp.web;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.service.RoleService;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final SecurityService securityService;

    public UserController(UserService userService, RoleService roleService, SecurityService securityService) {
        this.userService = userService;
        this.roleService = roleService;
        this.securityService = securityService;
    }

    @ModelAttribute("allRoles")
    public List<Role> roles() {
        return roleService.findAll();
    }

    @RequestMapping(value = "/user/{username}/view", method = RequestMethod.GET)
    public String getUserAccount(@PathVariable(value = "username") String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);

        return "user";
    }

    @RequestMapping(value = "/user/{username}/edit/info", method = RequestMethod.GET)
    public String editUserInfo(@PathVariable(value = "username") String username,
                               Model model) {

        UserDto user = new UserDto(userService.findByUsername(username));
        model.addAttribute("user", user);

        return "accountForm";

    }

    @RequestMapping(value = "/user/{username}/edit/info", method = RequestMethod.POST)
    public String updateUserInfo(@PathVariable(value = "username") String username,
                                 @RequestParam(value = "mode", required = false) String mode,
                                 @ModelAttribute("user") @Valid UserDto user,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "accountForm";
        }

        userService.update(user);

        if (mode != null && mode.equals("admin") && securityService.currentAuthenticationHasRole("ROLE_ADMIN")) {
            redirectAttributes.addAttribute("confirmation", "edited");
            return "redirect:/admin";
        } else {
            redirectAttributes.addAttribute("confirmation", "infoUpdated");
            return "redirect:/user/" + username + "/view";
        }
    }

    @RequestMapping(value = "/user/{username}/edit/password", method = RequestMethod.GET)
    public String editPassword(@PathVariable(value = "username") String username, Model model) {
        UserPasswordDto userPasswordDto = new UserPasswordDto();
        userPasswordDto.setUsername(username);

        model.addAttribute(userPasswordDto);

        return "passwordForm";
    }

    @RequestMapping(value = "/user/{username}/edit/password", method = RequestMethod.POST)
    public String updatePassword(@PathVariable("username") String username,
                                 @ModelAttribute("userPasswordDto") @Valid UserPasswordDto userPasswordDto,
                                 BindingResult result, RedirectAttributes redirectAttributes) {

        String currentPassword = userService.findByUsername(username).getPassword();

        if (!BCrypt.checkpw(userPasswordDto.getPassword(), currentPassword)) {
            result.rejectValue("password", null, "Current password is invalid");
        }

        if (result.hasErrors()) {
            return "passwordForm";
        }

        userService.updatePassword(userPasswordDto);

        redirectAttributes.addAttribute("confirmation", "passwordUpdated");

        return "redirect:/user/" + username + "/view";
    }

    @RequestMapping(value = "/user/{username}/delete", method = RequestMethod.GET)
    public String deleteAccount(@PathVariable(value = "username") String username) {

        User user = userService.findByUsername(username);
        userService.delete(user);

        return "redirect:/logout";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "loginForm";
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";
    }
}
