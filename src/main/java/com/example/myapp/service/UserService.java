package com.example.myapp.service;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.AddUserDTO;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.repository.RoleRepository;
import com.example.myapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityService securityService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDTOMapper userDTOMapper;

    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name or hasRole('ROLE_ADMIN')")
    public User findByUsername(String username) {
        return getUserByUsername(username);
    }

    @Transactional(readOnly = true)
    @PostAuthorize("returnObject.username == authentication.name or hasRole('ROLE_ADMIN')")
    public UserDTO findByGuid(UUID guid) {
        return userDTOMapper.map(getUserByGuid(guid));
    }

    @Transactional
    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Transactional
    public User getLoggedInUser() {
        UserDetails userDetails = securityService.getPrincipal();
        return getUserByUsername(userDetails.getUsername());
    }

    @Transactional
    public UserDTO getUserInContext() {
        UserDetails userDetails = securityService.getPrincipal();
        User user = getUserByUsername(userDetails.getUsername());

        return userDTOMapper.map(user);
    }

    @Transactional
    public Page<UserDTO> findAll(Pageable pageable, String search) {
        Page<User> page = StringUtils.isNotBlank(search) ? userRepository.search(pageable, search) :
                userRepository.findAll(pageable);

        return page.map(userDTOMapper::map);
    }

    @Transactional
    public User add(UserRegistrationDto userRegistrationDto) {

        User user = new User();
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setUsername(userRegistrationDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRegistrationDto.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findByTypeIn(userRegistrationDto.getRoleTypes())));

        return userRepository.save(user);
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    public User add(AddUserDTO addUserDTO) {
        User user = new User();
        user.setFirstName(addUserDTO.getFirstName());
        user.setLastName(addUserDTO.getLastName());
        user.setUsername(addUserDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(addUserDTO.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findByTypeIn(addUserDTO.getRoleTypes())));

        return userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("#userDTO.username == authentication.name or hasRole('ROLE_ADMIN')")
    public User update(UserDTO userDTO) {

        // The persisted user will automatically be updated in the database at the end of the transaction
        // without the need to call the DAO to issue an update.
        User user = getUserByUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        List<RoleType> updatedRoleTypes = userDTO.getRoleTypes();
        List<RoleType> currentRoleTypes = user.getRoles().stream().map(Role::getType).collect(Collectors.toList());

        // Add any new roles.
        for (RoleType roleType : updatedRoleTypes) {
            if (!currentRoleTypes.contains(roleType)) {
                user.getRoles().add(roleRepository.findByType(roleType));
            }
        }

        // Remove roles that were removed.
        user.getRoles().removeIf(role -> !updatedRoleTypes.contains(role.getType()));

        return user;
    }

    @Transactional
    @Secured("ROLE_ADMIN")
    public User update(UUID guid, UserDTO userDTO) {
        // The persisted user will automatically be updated in the database at the end of the transaction
        // without the need to call the DAO to issue an update.
        User user = getUserByGuid(guid);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        List<RoleType> updatedRoleTypes = userDTO.getRoleTypes();
        List<RoleType> currentRoleTypes = user.getRoles().stream().map(Role::getType).collect(Collectors.toList());

        // Add any new roles.
        for (RoleType roleType : updatedRoleTypes) {
            if (!currentRoleTypes.contains(roleType)) {
                user.getRoles().add(roleRepository.findByType(roleType));
            }
        }

        // Remove roles that were removed.
        user.getRoles().removeIf(role -> !updatedRoleTypes.contains(role.getType()));

        return user;
    }

    @Transactional
    @PreAuthorize("#userPasswordDto.username == authentication.name")
    public User updatePassword(UserPasswordDto userPasswordDto) {

        // The persisted user will automatically be updated in the database at the end of the transaction
        // without the need to call the DAO to issue an update.
        User user = getUserByUsername(userPasswordDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userPasswordDto.getNewPassword()));

        return user;
    }

    @Transactional
    @PreAuthorize("#username == authentication.name or hasRole('ROLE_ADMIN')")
    public void delete(String username) {
        userRepository.delete(getUserByUsername(username));
    }

    @Transactional
    public void delete(UUID guid) {
        userRepository.delete(getUserByGuid(guid));
    }

    private User getUserByGuid(UUID guid) {
        User user = userRepository.findByGuid(guid);

        if (user == null) {
            throw new EntityNotFoundException("User not found for guid: " + guid.toString());
        }

        return user;
    }

    private User getUserByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username."));
    }
}
