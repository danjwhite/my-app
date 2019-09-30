package com.example.myapp.dto;

import com.example.myapp.constraint.FieldMatch;
import com.example.myapp.domain.Role;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@FieldMatch(first = "password", second = "confirmPassword", message = "Password fields must match")
public class UserRegistrationDto {

    @NotBlank(message = "Cannot be blank")
    private String firstName;

    @NotBlank(message = "Cannot be blank")
    private String lastName;

    @NotBlank(message = "Cannot be blank")
    private String username;

    @NotBlank(message = "Cannot be blank")
    private String password;

    @NotBlank(message = "Cannot be blank")
    private String confirmPassword;

    @Size(min = 1, message = "At least one role must be selected")
    private Set<Role> roles = new LinkedHashSet<>(0);
}
