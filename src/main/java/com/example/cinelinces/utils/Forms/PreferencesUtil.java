package com.example.cinelinces.utils.Forms;

/*
 * Guarda/recupera preferencias de usuario.
 * Esta clase utiliza java.util.prefs.Preferences para almacenar
 * pequeñas cantidades de datos específicos del usuario, como el nombre de usuario recordado.
 * Los datos se almacenan de forma persistente en una ubicación dependiente del sistema operativo.
 */

import java.util.prefs.Preferences;

public class PreferencesUtil {
    // Se obtiene un nodo de preferencias específico para la clase PreferencesUtil.
    // Esto ayuda a evitar colisiones de nombres con otras aplicaciones o partes de la misma aplicación.
    private static final Preferences prefs =
            Preferences.userNodeForPackage(PreferencesUtil.class);

    // Clave utilizada para almacenar y recuperar el nombre de usuario recordado.
    private static final String REMEMBERED_USER_KEY = "rememberedUser";

    /**
     * Guarda el nombre de usuario (o email) proporcionado en las preferencias del usuario.
     * Este valor se puede recuperar más tarde para, por ejemplo, pre-rellenar el campo de inicio de sesión.
     *
     * @param user El nombre de usuario o email a recordar. Si es null, se guardará una cadena vacía.
     */
    public static void saveRememberedUser(String user) {
        if (user != null) {
            prefs.put(REMEMBERED_USER_KEY, user);
        } else {
            prefs.put(REMEMBERED_USER_KEY, ""); // Guardar cadena vacía si el usuario es null
        }
        // Opcionalmente, podrías querer forzar el guardado inmediato en disco, aunque no suele ser necesario:
        // try {
        //     prefs.flush();
        // } catch (BackingStoreException e) {
        //     System.err.println("Error al guardar preferencias de usuario: " + e.getMessage());
        //     e.printStackTrace();
        // }
    }

    /**
     * Recupera el nombre de usuario (o email) recordado de las preferencias del usuario.
     *
     * @return El nombre de usuario o email recordado. Devuelve una cadena vacía ("")
     * si no se encontró ningún usuario recordado o si nunca se ha guardado uno.
     */
    public static String getRememberedUser() {
        // El segundo argumento de prefs.get() es el valor por defecto si la clave no se encuentra.
        return prefs.get(REMEMBERED_USER_KEY, "");
    }

    /**
     * Elimina la preferencia del usuario recordado.
     * Esto es útil si el usuario desmarca la opción "Recordarme" o cierra sesión
     * y no desea que su nombre de usuario se recuerde la próxima vez.
     */
    public static void clearRememberedUser() {
        prefs.remove(REMEMBERED_USER_KEY);
        // Opcionalmente, podrías querer forzar el guardado inmediato en disco:
        // try {
        //     prefs.flush();
        // } catch (BackingStoreException e) {
        //     System.err.println("Error al limpiar preferencias de usuario: " + e.getMessage());
        //     e.printStackTrace();
        // }
    }

    // Podrías añadir otros métodos para guardar más preferencias si es necesario, por ejemplo:
    // public static void saveThemePreference(String themeName) {
    //     prefs.put("userTheme", themeName);
    // }
    //
    // public static String getThemePreference() {
    //     return prefs.get("userTheme", "default"); // "default" como tema por defecto
    // }
}