package com.example.myapp.service;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.service.mapper.UserDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInContextService {

    private final UserService userService;
    private final SecurityService securityService;
    private final UserDTOMapper userDTOMapper;

    @Transactional
    public UserDTO getUserInContext() {
        UserDetails userDetails = securityService.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());

        return userDTOMapper.mapToUserDTO(user);
    }
}
