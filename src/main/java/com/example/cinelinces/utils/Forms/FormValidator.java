package com.example.cinelinces.utils.Forms;

import java.util.regex.Pattern;
public class FormValidator {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,}$");

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean validateSignIn(String user, String pass) {
        if (user.isEmpty()) return false;
        if (pass.isEmpty() || pass.length() < 6) return false;
        return true;
    }

    public static boolean validateSignUp(
            String name, String email, String user,
            String pass, String confirm, boolean terms) {
        if (name.isEmpty())                           return false;
        if (!isValidEmail(email))                     return false;
        if (user.isEmpty() || user.length() < 3)      return false;
        if (pass.length() < 6)                        return false;
        if (!pass.equals(confirm))                    return false;
        if (!terms)                                   return false;
        return true;
    }
}
