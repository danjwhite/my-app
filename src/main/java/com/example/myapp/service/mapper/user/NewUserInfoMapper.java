package com.example.myapp.service.mapper.user;

import com.example.myapp.domain.User;
import com.example.myapp.dto.NewUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewUserInfoMapper {

    private final RolesMapper rolesMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User mapToUser(NewUserInfo source) {
        User user = new User();
        user.setFirstName(source.getFirstName());
        user.setLastName(source.getLastName());
        user.setUsername(source.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(source.getPassword()));
        user.setRoles(rolesMapper.mapToRoles(source.getRoleTypes()));

        return user;
    }
}
