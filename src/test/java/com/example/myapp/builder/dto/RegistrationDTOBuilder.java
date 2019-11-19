package com.example.myapp.builder.dto;

import com.example.myapp.builder.AbstractBuilder;
import com.example.myapp.dto.RegistrationDTO;

public class RegistrationDTOBuilder extends AbstractBuilder<RegistrationDTO> {

    private RegistrationDTOBuilder() {
        super(new RegistrationDTO());
    }

    public static RegistrationDTOBuilder givenRegistrationDTO() {
        return new RegistrationDTOBuilder();
    }

    public RegistrationDTOBuilder withFirstName(String firstName) {
        getObject().setFirstName(firstName);
        return this;
    }

    public RegistrationDTOBuilder withLastName(String lastName) {
        getObject().setLastName(lastName);
        return this;
    }

    public RegistrationDTOBuilder withUsername(String username) {
        getObject().setUsername(username);
        return this;
    }

    public RegistrationDTOBuilder withPassword(String password) {
        getObject().setPassword(password);
        return this;
    }

    public RegistrationDTOBuilder withConfirmPassword(String confirmPassword) {
        getObject().setConfirmPassword(confirmPassword);
        return this;
    }
}
