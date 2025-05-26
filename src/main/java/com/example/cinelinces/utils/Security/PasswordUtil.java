package com.example.cinelinces.utils.Security; // O el paquete que prefieras


import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // El "work factor" para BCrypt. Valores más altos son más seguros pero más lentos.
    // 10-12 es un buen punto de partida.
    private static final int BCRYPT_WORK_FACTOR = 10;

    /**
     * Genera un hash BCrypt para una contraseña dada.
     *
     * @param plainPassword La contraseña en texto plano.
     * @return El hash BCrypt de la contraseña.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía.");
        }
        String salt = BCrypt.gensalt(BCRYPT_WORK_FACTOR);
        return BCrypt.hashpw(plainPassword, salt);
    }

    /**
     * Verifica una contraseña en texto plano contra un hash BCrypt almacenado.
     *
     * @param plainPassword  La contraseña en texto plano ingresada por el usuario.
     * @param hashedPassword El hash almacenado en la base de datos.
     * @return true si la contraseña coincide, false en caso contrario.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || plainPassword.isEmpty() || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Esto puede ocurrir si hashedPassword no es un hash BCrypt válido
            System.err.println("Error al verificar la contraseña (formato de hash inválido): " + e.getMessage());
            return false;
        }
    }
}