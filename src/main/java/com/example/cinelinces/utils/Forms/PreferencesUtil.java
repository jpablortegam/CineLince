package com.example.cinelinces.utils.Forms;

/*
 * Guarda/recupera preferencias de usuario.
 */

import java.util.prefs.Preferences;

public class PreferencesUtil {
    private static final Preferences prefs =
            Preferences.userNodeForPackage(PreferencesUtil.class);

    public static void saveRememberedUser(String user) {
        prefs.put("rememberedUser", user);
    }

    public static String getRememberedUser() {
        return prefs.get("rememberedUser", "");
    }
}

