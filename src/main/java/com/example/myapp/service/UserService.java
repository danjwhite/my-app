package com.example.myapp.service;

import com.example.myapp.dao.RoleRepository;
import com.example.myapp.dao.UserRepository;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));
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

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }

    @Transactional
    public User add(UserRegistrationDto userRegistrationDto) {

        User user = new User();
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setUsername(userRegistrationDto.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRegistrationDto.getPassword()));

        for (Role role : userRegistrationDto.getRoles()) {
            Role userRole = roleRepository.findById(role.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found for id: " + role.getId()));
            user.getRoles().add(userRole);
        }

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

        Set<Role> newRoles = userDto.getRoles();
        Set<Role> originalRoles = user.getRoles();

        // Add any new roles.
        for (Role role : newRoles) {
            if (!originalRoles.contains(role)) {
                Role userRole = roleRepository.findById(role.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Role not found for id: " + role.getId()));
                user.getRoles().add(userRole);
            }
        }

        // Remove roles that were removed.
        user.getRoles().removeIf(role -> !newRoles.contains(role));

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
    @PreAuthorize("#user.username == authentication.name or hasRole('ROLE_ADMIN')")
    public void delete(User user) {
        userRepository.delete(user);
    }

    private User fetchByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username."));
    }
}
