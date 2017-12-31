package com.example.myapp.dao;

import com.example.myapp.domain.Role;

import java.util.List;

public interface IRoleDao {

    Role findById(long id);

    Role findByType(String type);

    List<Role> findAll();
}
