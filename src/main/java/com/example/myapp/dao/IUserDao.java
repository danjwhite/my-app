package com.example.myapp.dao;

import com.example.myapp.domain.User;

import java.util.List;

public interface IUserDao {

    User findById(long id);

    User findByUsername(String username);

    User add(User user);

    User update(User user);

    void delete(String username);

    List<User> findAll();

    long count();
}
