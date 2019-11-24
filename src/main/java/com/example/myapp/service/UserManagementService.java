package com.example.myapp.service;

import com.example.myapp.domain.User;
import com.example.myapp.dto.NewUserInfo;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.service.mapper.user.UserDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserService userService;
    private final RegistrationService registrationService;
    private final UserDTOMapper userDTOMapper;

    @Transactional
    public UserDTO findByGuid(UUID guid) {
        return userDTOMapper.mapToUserDTO(userService.findByGuid(guid));
    }

    @Transactional
    public Page<UserDTO> findAll(Pageable pageable) {
        return userService.findAll(pageable).map(userDTOMapper::mapToUserDTO);
    }

    @Transactional
    public Page<UserDTO> search(Pageable pageable, String search) {
        return userService.search(pageable, search).map(userDTOMapper::mapToUserDTO);
    }

    @Transactional
    public void addUser(NewUserInfo newUserInfo) {
        registrationService.registerUser(newUserInfo);
    }

    @Transactional
    public void updateUser(UUID guid, UserDTO userDTO) {
        User user = userService.findByGuid(guid);
        userDTOMapper.mapToUser(userDTO, user);
    }

    @Transactional
    public void deleteUser(UUID guid) {
        userService.delete(userService.findByGuid(guid));
    }
}
