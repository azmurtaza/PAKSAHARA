package com.example.paksahara.session;
import com.example.paksahara.model.User;
public class SessionManager {


    private static Integer currentModeratorId;

    public static void login(int userId, Integer moderatorId) {
        currentUserId = userId;
        currentModeratorId = moderatorId;
    }
    private static int currentUserId;
    private static String currentUserRole;

    public static void setCurrentUser(int userId, String role) {
        currentUserId   = userId;
        currentUserRole = role;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }
}