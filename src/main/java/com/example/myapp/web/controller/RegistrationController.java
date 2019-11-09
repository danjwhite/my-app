package com.example.myapp.web.controller;

import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import com.example.myapp.web.ResponseFactory;
import com.example.myapp.web.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;
    private final SecurityService securityService;

    public RegistrationController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<RestResponse> registerUser(@RequestBody @Valid UserRegistrationDto registrationDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.error(result);
        }

        if (userService.userExists(registrationDto.getUsername())) {
            result.rejectValue("username", "UsernameAlreadyTaken", "There is already an account registered with this username.");
            return ResponseFactory.error(result);
        }

        userService.add(registrationDto);
        securityService.autoLogin(registrationDto.getUsername(), registrationDto.getPassword());

        return ResponseFactory.success(HttpStatus.CREATED);
    }
}
