package com.example.myapp.web.controller;

import com.example.myapp.dto.AccountInfoDTO;
import com.example.myapp.dto.PasswordDTO;
import com.example.myapp.service.UserService;
import com.example.myapp.web.response.ResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/account-management")
@RequiredArgsConstructor
public class AccountManagementController {

    private final UserService userService;

    @GetMapping("/users/{guid}")
    public ResponseEntity<AccountInfoDTO> getAccountInfo(@PathVariable("guid") UUID guid) {
        AccountInfoDTO accountInfoDTO = userService.getAccountInfo(guid);
        return ResponseFactory.ok(accountInfoDTO);
    }

    @PutMapping("/users/{guid}")
    public ResponseEntity<?> updateAccountInfo(@PathVariable("guid") UUID guid, @RequestBody @Valid AccountInfoDTO accountInfoDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        userService.updateAccountInfo(guid, accountInfoDTO);

        return ResponseFactory.noContent();
    }

    @PutMapping("/users/{guid}/password")
    public ResponseEntity<?> updatePassword(@PathVariable("guid") UUID guid, @RequestBody @Valid PasswordDTO passwordDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        if (!userService.passwordMatches(guid, passwordDTO.getPassword())) {
            result.rejectValue("password", "InvalidPassword", "Current password is invalid.");
            return ResponseFactory.badRequest(result);
        }

        userService.updatePassword(guid, passwordDTO);

        return ResponseFactory.noContent();
    }

    @DeleteMapping("/users/{guid}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("guid") UUID guid, HttpServletRequest request, HttpServletResponse response) {
        userService.delete(guid);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return ResponseFactory.noContent();
    }
}
