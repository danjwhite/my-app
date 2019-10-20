package com.example.myapp.web.controller;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
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

    @GetMapping(value = "/user/{username}/view")
    public String getUserAccount(@PathVariable(value = "username") String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);

        return "user";
    }

    @GetMapping(value = "/user/{username}/edit/info")
    public String editUserInfo(@PathVariable(value = "username") String username,
                               Model model) {

        UserDto user = new UserDto(userService.findByUsername(username));
        model.addAttribute("user", user);

        return "accountForm";

    }

    @PostMapping(value = "/user/{username}/edit/info")
    public String updateUserInfo(@PathVariable(value = "username") String username,
                                 @RequestParam(value = "mode", required = false) String mode,
                                 @ModelAttribute("user") @Valid UserDto user,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "accountForm";
        }

        userService.update(user);

        if (mode != null && mode.equals("admin") && securityService.currentAuthenticationHasRole(RoleType.ROLE_ADMIN)) {
            redirectAttributes.addAttribute("confirmation", "edited");
            return "redirect:/admin";
        } else {
            redirectAttributes.addAttribute("confirmation", "infoUpdated");
            return "redirect:/user/" + username + "/view";
        }
    }

    @GetMapping(value = "/user/{username}/edit/password")
    public String editPassword(@PathVariable(value = "username") String username, Model model) {
        UserPasswordDto userPasswordDto = new UserPasswordDto();
        userPasswordDto.setUsername(username);

        model.addAttribute("userPasswordDto", userPasswordDto);

        return "passwordForm";
    }

    @PostMapping(value = "/user/{username}/edit/password")
    public String updatePassword(@PathVariable("username") String username,
                                 @ModelAttribute("userPasswordDto") @Valid UserPasswordDto userPasswordDto,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "passwordForm";
        }

        String currentPassword = userService.findByUsername(username).getPassword();

        if (!BCrypt.checkpw(userPasswordDto.getPassword(), currentPassword)) {
            result.rejectValue("password", "InvalidPassword", "Current password is invalid");
            return "passwordForm";
        }

        userService.updatePassword(userPasswordDto);

        redirectAttributes.addAttribute("confirmation", "passwordUpdated");

        return "redirect:/user/" + username + "/view";
    }

    @GetMapping(value = "/user/{username}/delete")
    public String deleteAccount(@PathVariable(value = "username") String username) {
        userService.delete(username);

        return "redirect:/logout";
    }
}
