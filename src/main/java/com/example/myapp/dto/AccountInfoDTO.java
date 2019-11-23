package com.example.myapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AccountInfoDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    @NotBlank(message = "Cannot be blank")
    private String firstName;

    @NotBlank(message = "Cannot be blank")
    private String lastName;
}
