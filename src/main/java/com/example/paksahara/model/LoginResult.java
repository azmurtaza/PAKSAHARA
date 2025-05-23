package com.example.paksahara.model;

public class LoginResult {
    public int userId;
    public String role;
    private final Integer moderatorId;

    // Single constructor with all fields
    public LoginResult(int userId, String role, Integer moderatorId) {
        this.userId = userId;
        this.role = role;
        this.moderatorId = moderatorId; // Final field initialized here
    }

    public int getUserId() { return userId; }
    public String getRole() { return role; }
    public Integer getModeratorId() { return moderatorId; }
}