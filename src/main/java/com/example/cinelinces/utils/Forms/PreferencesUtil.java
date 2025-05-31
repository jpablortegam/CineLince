package com.example.cinelinces.utils.Forms;


import java.util.prefs.Preferences;

public class PreferencesUtil {
    private static final Preferences prefs =
            Preferences.userNodeForPackage(PreferencesUtil.class);

    private static final String REMEMBERED_USER_KEY = "rememberedUser";

    public static void saveRememberedUser(String user) {
        if (user != null) {
            prefs.put(REMEMBERED_USER_KEY, user);
        } else {
            prefs.put(REMEMBERED_USER_KEY, "");
        }
    }

    public static String getRememberedUser() {
        return prefs.get(REMEMBERED_USER_KEY, "");
    }

    public static void clearRememberedUser() {
        prefs.remove(REMEMBERED_USER_KEY);
    }
}