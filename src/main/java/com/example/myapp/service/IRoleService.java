package com.example.myapp.service;

import com.example.myapp.domain.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {

    Optional<Role> findById(long id);

    Role findByType(String type);

    List<Role> findAll();
}
