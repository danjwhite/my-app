package com.example.myapp.dto;

import com.example.myapp.domain.RoleType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class UserDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID guid;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    @NotBlank(message = "Cannot be blank")
    private String firstName;

    @NotBlank(message = "Cannot be blank")
    private String lastName;

    @Size(min = 1, message = "At least one role must be selected.")
    private List<RoleType> roleTypes = new ArrayList<>(0);
}
