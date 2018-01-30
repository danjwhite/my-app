package com.example.myapp.dto;

import com.example.myapp.constraint.FieldMatch;
import org.hibernate.validator.constraints.NotBlank;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
