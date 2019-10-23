package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.dto.UserPasswordDto;

public class UserPasswordDtoBuilder extends AbstractBuilder<UserPasswordDto> {

    private UserPasswordDtoBuilder() {
        super(new UserPasswordDto());
    }

    public static UserPasswordDtoBuilder givenUserPasswordDto() {
        return new UserPasswordDtoBuilder();
    }

    public UserPasswordDtoBuilder withUsername(String username) {
        getObject().setUsername(username);
        return this;
    }

    public UserPasswordDtoBuilder withPassword(String password) {
        getObject().setPassword(password);
        return this;
    }

    public UserPasswordDtoBuilder withNewPassword(String newPassword) {
        getObject().setNewPassword(newPassword);
        return this;
    }

    public UserPasswordDtoBuilder withConfirmPassword(String confirmNewPassword) {
        getObject().setConfirmPassword(confirmNewPassword);
        return this;
    }
}
