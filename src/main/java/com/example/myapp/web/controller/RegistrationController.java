package com.example.myapp.web.controller;

import com.example.myapp.dto.RegistrationDTO;
import com.example.myapp.service.SecurityService;
import com.example.myapp.service.UserService;
import com.example.myapp.web.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final SecurityService securityService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationDTO registrationDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        if (userService.userExists(registrationDTO.getUsername())) {
            result.rejectValue("username", "UsernameAlreadyTaken", "There is already an account registered with this username.");
            return ResponseFactory.badRequest(result);
        }

        userService.registerUser(registrationDTO);
        securityService.autoLogin(registrationDTO.getUsername(), registrationDTO.getPassword());

        return ResponseFactory.noContent();
    }
}
