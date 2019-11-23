package com.example.myapp.service.mapper;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDTOMapper {

    private final RolesMapper rolesMapper;

    public UserDTO mapToUserDTO(User source) {
        UserDTO userDTO = new UserDTO();
        userDTO.setGuid(source.getGuid());
        userDTO.setUsername(source.getUsername());
        userDTO.setFirstName(source.getFirstName());
        userDTO.setLastName(source.getLastName());
        userDTO.setRoleTypes(rolesMapper.mapToRoleTypes(source.getRoles()));

        return userDTO;
    }

    public User mapToUser(UserDTO source, User target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setRoles(rolesMapper.mapToRoles(source.getRoleTypes(), target.getRoles()));

        return target;
    }
}
