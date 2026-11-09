package edu.utsa.cs3443.bowlbuddyproto.bowlbuddyproto.model;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Creates salted password hashes and verifies entered passwords.
 */
public final class PasswordUtil {

    private static final int ITERATIONS = 600_000;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);

        byte[] hash = deriveKey(password, salt, ITERATIONS);

        return "pbkdf2$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null) {
            return false;
        }

        try {
            String[] parts = storedHash.split("\\$", -1);

            if (parts.length != 4 || !parts[0].equals("pbkdf2")) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);

            if (iterations < ITERATIONS || iterations > 2_000_000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            if (salt.length != 16 || expectedHash.length != 32) {
                return false;
            }

            byte[] actualHash = deriveKey(password, salt, iterations);
            return MessageDigest.isEqual(expectedHash, actualHash);

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static byte[] deriveKey(String password, byte[] salt,
                                    int iterations) {
        PBEKeySpec specification =
                new PBEKeySpec(password.toCharArray(), salt, iterations, 256);

        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(specification)
                    .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "Password hashing is unavailable.", exception);
        } finally {
            specification.clearPassword();
        }
    }
}
