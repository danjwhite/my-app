package com.example.myapp.dto;

import com.example.myapp.constraint.FieldMatch;
import org.hibernate.validator.constraints.NotBlank;

@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "The password fields must match")
public class UserPasswordDto {

    private Long userId;

    @NotBlank
    private String password;

    @NotBlank
    private String newPassword;

    @NotBlank
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
