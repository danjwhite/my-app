package com.example.myapp.service;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDTOMapper {

    public UserDTO map(User user) {
        UserDTO dto = new UserDTO();
        dto.setGuid(user.getGuid());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());

        user.getRoles().forEach(role -> dto.getRoleTypes().add(role.getType()));

        return dto;
    }
}
