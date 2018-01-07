package com.example.myapp.service;

import com.example.myapp.dao.IUserDao;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    private IUserDao userDao;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(IUserDao userDao, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = userDao;
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
        user.setRoles(Arrays.asList(new Role("ROLE_USER")));
        return userDao.add(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        User entity = userDao.findById(user.getId());

        if (!entity.getPassword().equals(user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }

        return userDao.update(user);
    }

    @Override
    @Transactional
    public void delete(String username) {
        userDao.delete(username);
    }
}
