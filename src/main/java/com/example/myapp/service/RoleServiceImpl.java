package com.example.myapp.service;

import com.example.myapp.dao.RoleRepository;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements IRoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(long id) {
        return roleRepository.findById(id);
    }

    // TODO: Update to use enum parameter
    @Override
    @Transactional(readOnly = true)
    public Role findByType(String type) {
        return roleRepository.findByType(Enum.valueOf(RoleType.class, type));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return (List<Role>) roleRepository.findAll();
    }
}
