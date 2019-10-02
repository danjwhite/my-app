package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.domain.Role;
import com.example.myapp.dto.UserDto;

import java.util.Set;

public class UserDtoBuilder extends AbstractBuilder<UserDto> {

    private UserDtoBuilder() {
        super(new UserDto());
    }

    public static UserDtoBuilder givenUserDto() {
        return new UserDtoBuilder();
    }

    public UserDtoBuilder withUsername(String username) {
        getObject().setUsername(username);
        return this;
    }

    public UserDtoBuilder withFirstName(String firstName) {
        getObject().setFirstName(firstName);
        return this;
    }

    public UserDtoBuilder withLastName(String lastName) {
        getObject().setLastName(lastName);
        return this;
    }

    public UserDtoBuilder withRoles(Set<Role> roles) {
        getObject().setRoles(roles);
        return this;
    }
}
