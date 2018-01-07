package com.example.myapp.service;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserRegistrationDto;

import java.util.List;

public interface IUserService {

    User findById(long id);

    User findByUsername(String username);

    User add(UserRegistrationDto userRegistrationDto);

    User update(User user);

    void delete(String username);

    List<User> findAll();

    long count();
}
