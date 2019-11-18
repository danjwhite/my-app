package com.example.myapp.web.controller;

import com.example.myapp.dto.AddUserDTO;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.service.UserService;
import com.example.myapp.web.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/user-management")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getUsers(
            @PageableDefault(page = 0, size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "username", direction = Sort.Direction.ASC)
            }) Pageable pageable,
            @RequestParam(value = "search", required = false) String search) {

        Page<UserDTO> page = userService.findAll(pageable, search);
        return ResponseFactory.ok(page);
    }

    @GetMapping("/users/{guid}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(value = "guid") UUID guid) {
        UserDTO userDTO = userService.findByGuid(guid);
        return ResponseFactory.ok(userDTO);
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody @Valid AddUserDTO addUserDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        if (userService.userExists(addUserDTO.getUsername())) {
            result.rejectValue("username", "UsernameAlreadyTaken", "There is already an account registered with this username.");
            return ResponseFactory.badRequest(result);
        }

        userService.add(addUserDTO);

        return ResponseFactory.noContent();
    }

    @PutMapping("/users/{guid}")
    public ResponseEntity<?> updateUser(@PathVariable("guid") UUID guid, @RequestBody @Valid UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        userService.update(guid, userDTO);

        return ResponseFactory.noContent();
    }

    @DeleteMapping(value = "/users/{guid}")
    public ResponseEntity<Void> deleteUser(@PathVariable("guid") UUID guid) {
        userService.delete(guid);
        return ResponseFactory.noContent();
    }
}
