package com.example.myapp.builder.entity;

import com.example.myapp.builder.AbstractEntityBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;

import javax.persistence.EntityManager;
import java.util.Set;

public class UserBuilder extends AbstractEntityBuilder<User> {

    private UserBuilder(EntityManager entityManager) {
        super(new User(), entityManager);
    }

    public static UserBuilder givenUser() {
        return new UserBuilder(null);
    }

    public static UserBuilder givenUser(EntityManager entityManager) {
        return new UserBuilder(entityManager);
    }

    public UserBuilder withId(Long id) {
        getObject().setId(id);
        return this;
    }

    public UserBuilder withUsername(String username) {
        getObject().setUsername(username);
        return this;
    }

    public UserBuilder withPassword(String password) {
        getObject().setPassword(password);
        return this;
    }

    public UserBuilder withRoles(Set<Role> roles) {
        getObject().setRoles(roles);
        return this;
    }
}
