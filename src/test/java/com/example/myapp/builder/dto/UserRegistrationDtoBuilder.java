package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.domain.RoleType;
import com.example.myapp.dto.UserRegistrationDto;

import java.util.List;

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

    public UserRegistrationDtoBuilder withRoleTypes(List<RoleType> roleTypes) {
        getObject().setRoleTypes(roleTypes);
        return this;
    }
}
