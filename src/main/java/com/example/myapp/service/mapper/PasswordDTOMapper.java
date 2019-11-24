package com.example.myapp.service.mapper;

import com.example.myapp.domain.User;
import com.example.myapp.dto.PasswordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordDTOMapper {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void mapToUser(PasswordDTO source, User target) {
        target.setPassword(bCryptPasswordEncoder.encode(source.getNewPassword()));

    }
}
