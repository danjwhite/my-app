package com.example.myapp.dto;

import com.example.myapp.domain.User;
import org.hibernate.validator.constraints.NotBlank;

public class UserDto {

    private Long id;

    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    public UserDto() {

    }

    public UserDto(User user) {
        username = user.getUsername();
        firstName = user.getFirstName();
        lastName = user.getLastName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
