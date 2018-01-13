package com.example.myapp.service;

import com.example.myapp.dao.IRoleDao;
import com.example.myapp.dao.IUserDao;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    public User update(User user) {

        // Find user.
        User entity = userDao.findById(user.getId());

        if (entity != null) {

            // Set user entity fields to match any updated fields.
            entity.setFirstName(user.getFirstName());
            entity.setLastName(user.getLastName());
            entity.setUsername(user.getUsername());

            // Encode and set password if it dose not match.
            if (!bCryptPasswordEncoder.matches(user.getPassword(), entity.getPassword())) {
                entity.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }

            // Add any new roles.
            Set<Role> roles = entity.getRoles();
            for (Role role : user.getRoles()) {
                if (!roles.contains(role)) {
                    roles.add(role);
                }
            }

            entity.setRoles(roles);
        }

        return userDao.update(entity);
    }

    @Override
    @Transactional
    public void delete(String username) {
        userDao.delete(username);
    }
}
