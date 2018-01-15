package com.example.myapp.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface ISecurityService {

    UserDetails getPrincipal();

    boolean isCurrentAuthenticationAnonymous();
//
//    void autoLogin(String username, String password);
}
