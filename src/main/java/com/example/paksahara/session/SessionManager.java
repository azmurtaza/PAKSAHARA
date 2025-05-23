package com.example.paksahara.session;

public class SessionManager {
    private static int currentUserId;
    private static String currentUserRole;

    public static void setCurrentUser(int userId, String role) {
        currentUserId = userId;
        currentUserRole = role;
    }

    private static Integer currentModeratorId;

    public static void login(int userId, Integer moderatorId) {
        currentUserId = userId;
        currentModeratorId = moderatorId;
    }

    public static int getCurrentUserId() { return currentUserId; }
    public static Integer getCurrentModeratorId() { return currentModeratorId; }


    public static String getCurrentUserRole() {
        return currentUserRole;
    }
}