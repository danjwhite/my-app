package com.example.myapp.service;

import com.example.myapp.dao.IRoleDao;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements IUserService {

    private IUserDao userDao;

    private IRoleDao roleDao;

    private ISecurityService securityService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(IUserDao userDao, IRoleDao roleDao, ISecurityService securityService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
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

        Role role = roleDao.findByType("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        return userDao.add(user);
    }

    @Override
    @Transactional
    @PreAuthorize("#userDto.username == authentication.name or hasRole('ROLE_ADMIN')")
    public User update(UserDto userDto) {

        User user = findByUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        return userDao.update(user);
    }

    @Override
    @Transactional
    @PreAuthorize("#userPasswordDto.username == authentication.name")
    public User updatePassword(UserPasswordDto userPasswordDto) {

        User user = userDao.findByUsername(userPasswordDto.getUsername());

        if (user != null) {
            user.setPassword(bCryptPasswordEncoder.encode(userPasswordDto.getNewPassword()));
        }

        return userDao.update(user);
    }

    @Override
    @Transactional
    @PreAuthorize("#user.username == authentication.name or hasRole('ROLE_ADMIN')")
    public void delete(User user) {
        userDao.delete(user);
    }
}
