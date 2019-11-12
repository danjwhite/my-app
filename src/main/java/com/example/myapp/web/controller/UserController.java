package com.example.myapp.web.controller;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.UserService;
import com.example.myapp.web.ResponseFactory;
import com.example.myapp.web.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable(value = "username") String username) {
        User user = userService.findByUsername(username);
        return new ResponseEntity<>(new UserDto(user), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RestResponse> addUser(@RequestBody @Valid UserRegistrationDto registrationDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.error(result);
        }

        if (userService.userExists(registrationDto.getUsername())) {
            result.rejectValue("username", "UsernameAlreadyTaken", "There is already an account registered with this username.");
            return ResponseFactory.error(result);
        }

        userService.add(registrationDto);

        return ResponseFactory.success(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<RestResponse> updateUser(@RequestBody @Valid UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.error(result);
        }

        userService.update(userDto);

        return ResponseFactory.success(HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<RestResponse> updatePassword(@RequestBody @Valid UserPasswordDto passwordDto,
                                                       BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.error(result);
        }

        String currentPassword = userService.findByUsername(passwordDto.getUsername()).getPassword();
        if (!BCrypt.checkpw(passwordDto.getPassword(), currentPassword)) {
            result.rejectValue("password", "InvalidPassword", "Current password is invalid.");
            return ResponseFactory.error(result);
        }

        userService.updatePassword(passwordDto);
        return ResponseFactory.success(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{username}")
    public ResponseEntity<RestResponse> deleteUser(@PathVariable("username") String username) {
        userService.delete(username);
        return ResponseFactory.success(HttpStatus.OK);
    }
}
