package com.example.paksahara.model;

public interface Authenticable {
    boolean login(String email, String password);
    void logout();
}
