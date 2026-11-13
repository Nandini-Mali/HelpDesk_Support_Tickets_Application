package com.nexora.helpdesk.desktop.util;

import com.nexora.helpdesk.desktop.model.UserSession;

import java.util.Arrays;

public final class SessionManager {

    private static UserSession user;
    private static String email;
    private static char[] password;

    private SessionManager() {
    }

    public static void start(
            UserSession userSession,
            String userEmail,
            char[] userPassword) {

        clear();

        user = userSession;
        email = userEmail;
        password = userPassword.clone();
    }

    public static UserSession getUser() {
        return user;
    }

    public static String getEmail() {
        return email;
    }

    public static char[] getPassword() {
        return password == null ? null : password.clone();
    }

    public static void clear() {

        if (password != null) {
            Arrays.fill(password, '\0');
        }

        password = null;
        email = null;
        user = null;
    }
}