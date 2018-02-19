package com.example.myapp.dto;

import com.example.myapp.constraint.FieldMatch;
import com.example.myapp.domain.Role;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
