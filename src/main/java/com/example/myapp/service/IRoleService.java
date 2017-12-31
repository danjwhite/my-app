package com.example.myapp.service;

import com.example.myapp.domain.Role;

import java.util.List;

public interface IRoleService {

    Role findById(long id);

    Role findByType(String type);

    List<Role> findAll();
}
