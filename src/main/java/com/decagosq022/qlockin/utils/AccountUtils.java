package com.decagosq022.qlockin.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Component
public class AccountUtils {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    private static final Set<String> generatedIds = new HashSet<>();

    public static String generateUniqueId() {
        String uniqueId;
        do {
            StringBuilder sb = new StringBuilder();
            // Generate the first two letters
            for (int i = 0; i < 2; i++) {
                int letterIndex = random.nextInt(LETTERS.length());
                sb.append(LETTERS.charAt(letterIndex));
            }

            // Generate the remaining six digits
            for (int i = 0; i < 6; i++) {
                int digitIndex = random.nextInt(DIGITS.length());
                sb.append(DIGITS.charAt(digitIndex));
            }

            uniqueId = sb.toString();
        } while (generatedIds.contains(uniqueId));  // Ensure uniqueness

        generatedIds.add(uniqueId);
        return uniqueId;
    }

}
