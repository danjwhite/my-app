package com.example.myapp.converter;

import com.example.myapp.domain.Role;
import com.example.myapp.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
public class RoleConverter implements Converter<Object, Role> {

    @Autowired
    private IRoleService roleService;

    // TODO: Test
    @Override
    public Role convert(Object arg) {
        Long id = Long.parseLong((String) arg);

        return roleService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found for id: " + id));
    }
}
