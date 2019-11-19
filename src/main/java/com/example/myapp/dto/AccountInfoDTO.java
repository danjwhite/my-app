package com.example.myapp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AccountInfoDTO {

    private String username;

    @NotBlank(message = "Cannot be blank")
    private String firstName;

    @NotBlank(message = "Cannot be blank")
    private String lastName;
}
