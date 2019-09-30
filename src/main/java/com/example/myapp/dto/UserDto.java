package com.example.myapp.dto;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class UserDto {

    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Size(min = 1, message = "At least one role must be selected")
    private Set<Role> roles = new LinkedHashSet<>(0);

    public UserDto(User user) {
        username = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        roles = user.getRoles();
    }
}
