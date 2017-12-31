package com.example.myapp.domain;

import java.io.Serializable;

public enum RoleType implements Serializable {

    USER("USER"),
    ADMIN("ADMIN");

    String roleType;

    RoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getRoleType() {
        return roleType;
    }
}
