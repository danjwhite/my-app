package com.example.myapp.service;

import com.example.myapp.dao.IRoleDao;
import com.example.myapp.dao.IUserDao;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements IUserService {

    private IUserDao userDao;

    private IRoleDao roleDao;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(IUserDao userDao, IRoleDao roleDao, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(long id) {
        return userDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
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
    public User update(UserDto userDto) {

        // Find user.
        User user = userDao.findById(userDto.getId());

        if (user != null) {

            // Set user fields to match any updated fields.
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setUsername(userDto.getUsername());

            // Encode and set password if it dose not match.
            if (!BCrypt.checkpw(userDto.getPassword(), user.getPassword()))
                user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

            // Add any new roles.
            Set<Role> roles = user.getRoles();
            for (Role role : userDto.getRoles()) {
                if (!roles.contains(role)) {
                    roles.add(role);
                }
            }

            user.setRoles(roles);
        }

        return userDao.update(user);
    }

    @Override
    @Transactional
    public void delete(String username) {
        userDao.delete(username);
    }
}
