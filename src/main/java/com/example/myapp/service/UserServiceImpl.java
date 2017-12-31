package com.example.myapp.service;

import com.example.myapp.dao.IUserDao;
import com.example.myapp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public class UserServiceImpl implements IUserService {

    private IUserDao userDao;

    @Autowired
    public UserServiceImpl(IUserDao userDao) {
        this.userDao = userDao;
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
    public User add(User user) {
        return userDao.add(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        return userDao.update(user);
    }

    @Override
    @Transactional
    public void delete(String username) {
        userDao.delete(username);
    }
}
