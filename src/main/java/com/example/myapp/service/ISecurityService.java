package com.example.myapp.service;

public interface ISecurityService {

    String findLoggedInUsername();

    boolean isCurrentAuthenticationAnonymous();

    void autoLogin(String username, String password);
}
