package com.example.myapp.service;

public interface ISecurityService {

    String findLoggedInUsername();
    
    void autoLogin(String username, String password);
}
