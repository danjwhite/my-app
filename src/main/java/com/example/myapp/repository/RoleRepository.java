package com.example.myapp.repository;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Role findByType(RoleType type);

    List<Role> findByTypeIn(List<RoleType> roleTypes);
}
