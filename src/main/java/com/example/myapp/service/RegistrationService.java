package com.example.myapp.service;

import com.example.myapp.dto.NewUserInfo;
import com.example.myapp.service.exception.UsernameTakenException;
import com.example.myapp.service.mapper.user.NewUserInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final NewUserInfoMapper newUserInfoMapper;

    @Transactional
    public void registerUser(NewUserInfo newUserInfo) {
        if (userService.findByUsername(newUserInfo.getUsername()).isPresent()) {
            throw new UsernameTakenException();
        }

        userService.save(newUserInfoMapper.mapToUser(newUserInfo));
    }
}
