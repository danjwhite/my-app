package com.example.myapp.dto;

import com.example.myapp.constraint.BlankCheck;
import com.example.myapp.constraint.FieldMatch;
import com.example.myapp.constraint.MatchCheck;
import com.example.myapp.domain.RoleType;
import lombok.Data;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

// TODO: Add more validations
@Data
@GroupSequence({UserRegistrationDto.class, BlankCheck.class, MatchCheck.class})
@FieldMatch(first = "password", second = "confirmPassword", message = "Password fields must match.",
        groups = MatchCheck.class)
public class UserRegistrationDto {

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String firstName;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String lastName;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String username;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String password;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String confirmPassword;

    @Size(min = 1, message = "At least one role must be selected.")
    private List<RoleType> roleTypes = new ArrayList<>(0);
}
