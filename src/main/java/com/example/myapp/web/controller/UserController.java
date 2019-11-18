package com.example.myapp.web.controller;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.service.UserService;
import com.example.myapp.web.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(value = "username") String username) {
        User user = userService.findByUsername(username);
        return ResponseFactory.ok(new UserDTO(user));
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        userService.update(userDTO);

        return ResponseFactory.noContent();
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UserPasswordDto passwordDto,
                                                           BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        String currentPassword = userService.findByUsername(passwordDto.getUsername()).getPassword();
        if (!BCrypt.checkpw(passwordDto.getPassword(), currentPassword)) {
            result.rejectValue("password", "InvalidPassword", "Current password is invalid.");
            return ResponseFactory.badRequest(result);
        }

        userService.updatePassword(passwordDto);
        return ResponseFactory.noContent();
    }

    @DeleteMapping(value = "/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable("username") String username) {
        userService.delete(username);
        return ResponseFactory.noContent();
    }
}
