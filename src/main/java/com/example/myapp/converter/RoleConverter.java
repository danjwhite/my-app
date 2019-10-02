package com.example.myapp.converter;

import com.example.myapp.domain.Role;
import com.example.myapp.service.RoleService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
public class RoleConverter implements Converter<Object, Role> {

    private final RoleService roleService;

    public RoleConverter(RoleService roleService) {
        this.roleService = roleService;
    }

    // TODO: Test
    @Override
    public Role convert(Object arg) {
        Long id = Long.parseLong((String) arg);

        return roleService.findById(id);
    }
}
