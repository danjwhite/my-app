package com.example.myapp.web.controller;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.service.UserService;
import com.example.myapp.web.response.ResponseFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
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

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @PageableDefault(page = 0, size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC),
                    @SortDefault(sort = "username", direction = Sort.Direction.ASC)
            }) Pageable pageable,
            @RequestParam(value = "search", required = false) String search) {

        return ResponseFactory.ok(userService.findAll(pageable, search));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable(value = "username") String username) {
        User user = userService.findByUsername(username);
        return ResponseFactory.ok(new UserDto(user));
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody @Valid UserRegistrationDto registrationDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        if (userService.userExists(registrationDto.getUsername())) {
            result.rejectValue("username", "UsernameAlreadyTaken", "There is already an account registered with this username.");
            return ResponseFactory.badRequest(result);
        }

        userService.add(registrationDto);

        return ResponseFactory.noContent();
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        userService.update(userDto);

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
