package com.example.myapp.dao;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Role findByType(RoleType type);
}
