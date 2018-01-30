package com.example.myapp.dto;

import com.example.myapp.constraint.BlankCheck;
import com.example.myapp.constraint.FieldMatch;
import com.example.myapp.constraint.MatchCheck;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.GroupSequence;

@GroupSequence({UserPasswordDto.class, BlankCheck.class, MatchCheck.class})
@FieldMatch(first = "newPassword", second = "confirmNewPassword",
        message = "The password fields must match",
        groups = MatchCheck.class)
public class UserPasswordDto {

    private Long userId;

    private String password;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String newPassword;

    @NotBlank(message = "Cannot be blank", groups = BlankCheck.class)
    private String confirmNewPassword;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
