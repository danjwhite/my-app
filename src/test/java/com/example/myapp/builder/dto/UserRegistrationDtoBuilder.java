package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.dto.UserRegistrationDto;

import java.util.Set;

public class UserRegistrationDtoBuilder extends AbstractBuilder<UserRegistrationDto> {

    private UserRegistrationDtoBuilder() {
        super(new UserRegistrationDto());
    }

    public static UserRegistrationDtoBuilder givenUserRegistrationDto() {
        return new UserRegistrationDtoBuilder();
    }

    public UserRegistrationDtoBuilder withFirstName(String firstName) {
        getObject().setFirstName(firstName);
        return this;
    }

    public UserRegistrationDtoBuilder withLastName(String lastName) {
        getObject().setLastName(lastName);
        return this;
    }

    public UserRegistrationDtoBuilder withUsername(String username) {
        getObject().setUsername(username);
        return this;
    }

    public UserRegistrationDtoBuilder withPassword(String password) {
        getObject().setPassword(password);
        return this;
    }

    public UserRegistrationDtoBuilder withConfirmPassword(String confirmPassword) {
        getObject().setConfirmPassword(confirmPassword);
        return this;
    }

    public UserRegistrationDtoBuilder withRoles(Set<Role> roles) {
        getObject().setRoles(roles);
        return this;
    }
}
