package com.example.myapp.service;

import com.example.myapp.dao.IRoleDao;
import com.example.myapp.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class RoleServiceImpl implements IRoleService {

    private IRoleDao roleDao;

    @Autowired
    public RoleServiceImpl(IRoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Role findById(long id) {
        return roleDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByType(String type) {
        return roleDao.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleDao.findAll();
    }
}
