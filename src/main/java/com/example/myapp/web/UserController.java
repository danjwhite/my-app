package com.example.myapp.web;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private IUserService userService;

    @RequestMapping(value = "/account/view", method = RequestMethod.GET)
    public String getUserAccount(@RequestParam(value = "username") String username, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);

        return "user";
    }

    @RequestMapping(value = "/account/edit/info", method = RequestMethod.GET)
    public String editUserInfo(@RequestParam(value = "username") String username,
                               Model model) {

        UserDto user = new UserDto(userService.findByUsername(username));
        model.addAttribute("user", user);

        return "accountForm";

    }

    @RequestMapping(value = "account/edit/info", method = RequestMethod.POST)
    public String updateUserInfo(@ModelAttribute("user") @Valid UserDto user,
                                 BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "accountForm";
        }

        userService.update(user);
        redirectAttributes.addAttribute("username", user.getUsername());
        redirectAttributes.addAttribute("confirmation", "infoUpdated");

        return "redirect:/account/view";
    }

    @RequestMapping(value = "/account/edit/password", method = RequestMethod.GET)
    public String editPassword(@RequestParam(value = "username") String username, Model model) {
        UserPasswordDto userPasswordDto = new UserPasswordDto();
        userPasswordDto.setUsername(username);

        model.addAttribute(userPasswordDto);

        return "passwordForm";
    }

    @RequestMapping(value = "/account/edit/password", method = RequestMethod.POST)
    public String updatePassword(@ModelAttribute("userPasswordDto") @Valid UserPasswordDto userPasswordDto,
                                 BindingResult result, RedirectAttributes redirectAttributes) {

        String username = userPasswordDto.getUsername();
        String currentPassword = userService.findByUsername(username).getPassword();

        if (!BCrypt.checkpw(userPasswordDto.getPassword(), currentPassword)) {
            result.rejectValue("password", null, "Current password is invalid");
        }

        if (result.hasErrors()) {
            return "passwordForm";
        }

        userService.updatePassword(userPasswordDto);

        redirectAttributes.addAttribute("username", username);
        redirectAttributes.addAttribute("confirmation", "passwordUpdated");

        return "redirect:/account/view";
    }

    @RequestMapping(value = "/account/delete", method = RequestMethod.GET)
    public String deleteAccount(@RequestParam(value = "username") String username) {
        userService.delete(username);

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
