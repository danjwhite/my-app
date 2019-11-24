package com.example.myapp.service;

import com.example.myapp.domain.User;
import com.example.myapp.dto.AccountInfoDTO;
import com.example.myapp.dto.PasswordDTO;
import com.example.myapp.service.exception.PasswordMatchException;
import com.example.myapp.service.mapper.user.AccountInfoDTOMapper;
import com.example.myapp.service.mapper.user.PasswordDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountManagementService {

    private final UserService userService;
    private final AccountInfoDTOMapper accountInfoDTOMapper;
    private final PasswordDTOMapper passwordDTOMapper;

    @Transactional
    public AccountInfoDTO getAccountInfo(UUID guid) {
        User user = userService.findByGuid(guid);
        return accountInfoDTOMapper.mapToAccountInfoDTO(user);
    }

    @Transactional
    public void updateAccountInfo(UUID guid, AccountInfoDTO accountInfoDTO) {
        User user = userService.findByGuid(guid);
        accountInfoDTOMapper.mapToUser(accountInfoDTO, user);
    }

    @Transactional
    public void updatePassword(UUID guid, PasswordDTO passwordDTO) {
        User user = userService.findByGuid(guid);
        if (!BCrypt.checkpw(passwordDTO.getPassword(), user.getPassword())) {
            throw new PasswordMatchException();
        }

        passwordDTOMapper.mapToUser(passwordDTO, user);
    }

    @Transactional
    public void deleteAccount(UUID guid) {
        userService.delete(userService.findByGuid(guid));
    }
}
