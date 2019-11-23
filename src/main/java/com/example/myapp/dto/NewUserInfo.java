package com.example.myapp.dto;

import com.example.myapp.domain.RoleType;

import java.util.List;

public interface NewUserInfo {

    String getFirstName();

    String getLastName();

    String getUsername();

    String getPassword();

    List<RoleType> getRoleTypes();
}
