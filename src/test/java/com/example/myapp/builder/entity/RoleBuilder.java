package com.example.myapp.builder.entity;

import com.example.myapp.builder.AbstractEntityBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.RoleType;

import javax.persistence.EntityManager;

public class RoleBuilder extends AbstractEntityBuilder<Role> {

    private RoleBuilder(EntityManager entityManager) {
        super(new Role(), entityManager);
    }

    public static RoleBuilder givenRole() {
        return new RoleBuilder(null);
    }

    public static RoleBuilder givenRole(EntityManager entityManager) {
        return new RoleBuilder(entityManager);
    }

    public RoleBuilder withId(Long id) {
        getObject().setId(id);
        return this;
    }

    public RoleBuilder withType(RoleType roleType) {
        getObject().setType(roleType);
        return this;
    }
}
