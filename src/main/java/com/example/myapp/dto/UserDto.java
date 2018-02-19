package com.example.myapp.dto;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserDto {

    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Size(min = 1, message = "At least one role must be selected")
    private Set<Role> roles = new LinkedHashSet<>(0);

    public UserDto() {
    }

    public UserDto(User user) {
        username = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        roles = user.getRoles();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
