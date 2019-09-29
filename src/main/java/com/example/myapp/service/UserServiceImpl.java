package com.example.myapp.service;

import com.example.myapp.dao.RoleRepository;
import com.example.myapp.dao.IUserDao;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements IUserService {

    private IUserDao userDao;

    private RoleRepository roleRepository;

    private ISecurityService securityService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(IUserDao userDao, RoleRepository roleRepository, ISecurityService securityService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = userDao;
        this.roleRepository = roleRepository;
        this.securityService = securityService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(long id) {
        return userDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#username == authentication.name or hasRole('ROLE_ADMIN')")
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @Transactional
    public boolean userExists(String username) {
        return userDao.findByUsername(username) != null;
    }

    @Override
    @Transactional
    public User getLoggedInUser() {
        UserDetails userDetails = securityService.getPrincipal();
        return userDao.findByUsername(userDetails.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return userDao.count();
    }

    @Override
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

        return userDao.add(user);
    }

    @Override
    @Transactional
    @PreAuthorize("#userDto.username == authentication.name or hasRole('ROLE_ADMIN')")
    public User update(UserDto userDto) {


        // The persisted user will automatically be updated in the database at the end of the transaction
        // without the need to call the DAO to issue an update.
        User user = findByUsername(userDto.getUsername());
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

    @Override
    @Transactional
    @PreAuthorize("#userPasswordDto.username == authentication.name")
    public User updatePassword(UserPasswordDto userPasswordDto) {

        // The persisted user will automatically be updated in the database at the end of the transaction
        // without the need to call the DAO to issue an update.
        User user = userDao.findByUsername(userPasswordDto.getUsername());

        if (user != null) {
            user.setPassword(bCryptPasswordEncoder.encode(userPasswordDto.getNewPassword()));
        }

        return user;
    }

    @Override
    @Transactional
    @PreAuthorize("#user.username == authentication.name or hasRole('ROLE_ADMIN')")
    public void delete(User user) {
        userDao.delete(user);
    }
}
