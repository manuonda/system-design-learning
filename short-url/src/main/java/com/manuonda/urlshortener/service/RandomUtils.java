package com.manuonda.urlshortener.service;

import java.security.SecureRandom;

public class RandomUtils {


    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_KEY_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder(DEFAULT_KEY_LENGTH);
        for (int i = 0; i < DEFAULT_KEY_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(ALPHANUMERIC_CHARACTERS.length());
            sb.append(ALPHANUMERIC_CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

}
