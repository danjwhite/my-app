package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.domain.RoleType;
import com.example.myapp.dto.UserDTO;

import java.util.List;

public class UserDTOBuilder extends AbstractBuilder<UserDTO> {

    private UserDTOBuilder() {
        super(new UserDTO());
    }

    public static UserDTOBuilder givenUserDto() {
        return new UserDTOBuilder();
    }

    public UserDTOBuilder withUsername(String username) {
        getObject().setUsername(username);
        return this;
    }

    public UserDTOBuilder withFirstName(String firstName) {
        getObject().setFirstName(firstName);
        return this;
    }

    public UserDTOBuilder withLastName(String lastName) {
        getObject().setLastName(lastName);
        return this;
    }

    public UserDTOBuilder withRoleTypes(List<RoleType> roleTypes) {
        getObject().setRoleTypes(roleTypes);
        return this;
    }
}
