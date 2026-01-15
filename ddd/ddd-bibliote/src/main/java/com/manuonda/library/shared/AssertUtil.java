package com.manuonda.library.shared;

import java.util.regex.Pattern;

public class AssertUtil {
    private AssertUtil() {}

    public static void requireNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireMin(int value, int min, String message) {
        if (value < min) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireMin(long value, int min, String message) {
        if (value < min) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireSize(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " value must be between " + min + " and " + max);
        }
    }

    public static void requireNotBlank(String value, String fieldName) {
        if(value == null || value.trim().isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
    }

    public static void requireSize(String value, int min, int max, String fieldName) {
        if(value == null) {
            return;
        }
        if(value.length() < min || value.length() > max) {
            throw new IllegalArgumentException(fieldName + " size must be between " + min + " and " + max);
        }
    }

    public static void requirePattern(String value, String regex, String message) {
        if(value == null) {
            return;
        }
        if(!value.matches(regex)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requirePattern(String value, Pattern pattern, String message) {
        if(value == null) {
            return;
        }
        if(!pattern.matcher(value).matches()) {
            throw new IllegalArgumentException(message);
        }
    }
}