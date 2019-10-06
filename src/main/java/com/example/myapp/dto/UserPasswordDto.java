package com.example.myapp.dto;

import com.example.myapp.constraint.BlankCheck;
import com.example.myapp.constraint.FieldMatch;
import com.example.myapp.constraint.MatchCheck;
import lombok.Data;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotBlank;

@Data
@GroupSequence({UserPasswordDto.class, BlankCheck.class, MatchCheck.class})
@FieldMatch(first = "newPassword", second = "confirmNewPassword",
        message = "The password fields must match",
        groups = MatchCheck.class)
public class UserPasswordDto {

    private String username;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String password;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String newPassword;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String confirmNewPassword;
}
