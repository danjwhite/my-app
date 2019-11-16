package com.example.myapp.service;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import com.example.myapp.repository.RoleRepository;
import com.example.myapp.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SecurityService securityService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, SecurityService securityService,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.securityService = securityService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name or hasRole('ROLE_ADMIN')")
    public User findByUsername(String username) {
        return fetchByUsername(username);
    }

    @Transactional
    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Transactional
    public User getLoggedInUser() {
        UserDetails userDetails = securityService.getPrincipal();
        return fetchByUsername(userDetails.getUsername());
    }

    @Transactional
    public Page<User> findAll(Pageable pageable, String search) {
        return StringUtils.isNotBlank(search) ? userRepository.search(pageable, search) :
                userRepository.findAll(pageable);
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
    @PreAuthorize("#userDto.username == authentication.name or hasRole('ROLE_ADMIN')")
    public User update(UserDto userDto) {

        // The persisted user will automatically be updated in the database at the end of the transaction
        // without the need to call the DAO to issue an update.
        User user = fetchByUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        List<RoleType> updatedRoleTypes = userDto.getRoleTypes();
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
        User user = fetchByUsername(userPasswordDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userPasswordDto.getNewPassword()));

        return user;
    }

    @Transactional
    @PreAuthorize("#username == authentication.name or hasRole('ROLE_ADMIN')")
    public void delete(String username) {
        userRepository.delete(fetchByUsername(username));
    }

    private User fetchByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username."));
    }
}
