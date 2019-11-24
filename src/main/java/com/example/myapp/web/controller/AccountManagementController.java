package com.example.myapp.web.controller;

import com.example.myapp.dto.AccountInfoDTO;
import com.example.myapp.dto.PasswordDTO;
import com.example.myapp.service.AccountManagementService;
import com.example.myapp.service.exception.PasswordMatchException;
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

    private final AccountManagementService accountManagementService;

    @GetMapping("/users/{guid}")
    public ResponseEntity<AccountInfoDTO> getAccountInfo(@PathVariable("guid") UUID guid) {
        AccountInfoDTO accountInfoDTO = accountManagementService.getAccountInfo(guid);
        return ResponseFactory.ok(accountInfoDTO);
    }

    @PutMapping("/users/{guid}")
    public ResponseEntity<?> updateAccountInfo(@PathVariable("guid") UUID guid, @RequestBody @Valid AccountInfoDTO accountInfoDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        accountManagementService.updateAccountInfo(guid, accountInfoDTO);

        return ResponseFactory.noContent();
    }

    @PutMapping("/users/{guid}/password")
    public ResponseEntity<?> updatePassword(@PathVariable("guid") UUID guid, @RequestBody @Valid PasswordDTO passwordDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseFactory.badRequest(result);
        }

        try {
            accountManagementService.updatePassword(guid, passwordDTO);
        } catch (PasswordMatchException e) {
            result.rejectValue("password", "InvalidPassword", "Current password is invalid.");
            return ResponseFactory.badRequest(result);
        }

        return ResponseFactory.noContent();
    }

    @DeleteMapping("/users/{guid}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("guid") UUID guid, HttpServletRequest request, HttpServletResponse response) {
        accountManagementService.deleteAccount(guid);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return ResponseFactory.noContent();
    }
}
