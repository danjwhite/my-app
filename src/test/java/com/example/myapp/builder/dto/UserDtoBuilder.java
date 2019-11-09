package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.domain.RoleType;
import com.example.myapp.dto.UserDto;

import java.util.List;

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

    public UserDtoBuilder withRoleTypes(List<RoleType> roleTypes) {
        getObject().setRoleTypes(roleTypes);
        return this;
    }
}
