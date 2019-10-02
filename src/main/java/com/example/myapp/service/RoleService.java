package com.example.myapp.service;

import com.example.myapp.repository.RoleRepository;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Role findById(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found for id: " + id));
    }

    @Transactional(readOnly = true)
    public Role findByType(RoleType type) {
        return Optional.ofNullable(roleRepository.findByType(type))
                .orElseThrow(() -> new EntityNotFoundException("Role not found for type: " + type.name()));
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return (List<Role>) roleRepository.findAll();
    }
}
