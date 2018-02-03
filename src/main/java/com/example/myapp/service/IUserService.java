package com.example.myapp.service;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDto;
import com.example.myapp.dto.UserPasswordDto;
import com.example.myapp.dto.UserRegistrationDto;

import java.util.List;

public interface IUserService {

    User findById(long id);

    User findByUsername(String username);

    User getLoggedInUser();

    User add(UserRegistrationDto userRegistrationDto);

    User update(UserDto userDto);

    User updatePassword(UserPasswordDto userPasswordDto);

    void delete(long id);

    List<User> findAll();

    long count();
}
