package com.store.app.petstore.Utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Vietnamese phone validation pattern
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+84|0)[1-9][0-9]{8,9}$"
    );
    
    // Name validation pattern (Vietnamese characters)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-ZÀ-ỹ\\s]{2,100}$"
    );
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate Vietnamese phone number format
     * Supports formats: 0xxxxxxxxx, +84xxxxxxxxx
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        
        String cleanPhone = phone.trim().replaceAll("\\s+", "");
        
        // Check basic pattern
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            return false;
        }
        
        // Additional validation for Vietnamese mobile numbers
        if (cleanPhone.startsWith("0")) {
            // Mobile numbers: 09x, 08x, 07x, 05x, 03x
            String prefix = cleanPhone.substring(0, 3);
            return prefix.matches("^0(3[2-9]|5[689]|7[06-9]|8[1-689]|9[0-46-9])$");
        } else if (cleanPhone.startsWith("+84")) {
            // International format
            String localPart = cleanPhone.substring(3);
            return localPart.matches("^[3579][0-9]{8}$");
        }
        
        return false;
    }
    
    /**
     * Validate name format (Vietnamese characters allowed)
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }
    
    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6 || password.length() > 50) {
            return false;
        }
        
        // Check for at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        
        return hasLetter && hasNumber;
    }
    
    /**
     * Sanitize input string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        return input.trim()
                   .replaceAll("[<>\"'&]", "") // Remove potentially dangerous characters
                   .replaceAll("\\s+", " ");   // Replace multiple spaces with single space
    }
    
    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string length is within range
     */
    public static boolean isLengthValid(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Format phone number for display
     */
    public static String formatPhoneNumber(String phone) {
        if (phone == null) return "";
        
        String cleanPhone = phone.trim().replaceAll("\\s+", "");
        
        if (cleanPhone.startsWith("0") && cleanPhone.length() == 10) {
            // Format: 0xxx xxx xxx
            return cleanPhone.substring(0, 4) + " " + 
                   cleanPhone.substring(4, 7) + " " + 
                   cleanPhone.substring(7);
        } else if (cleanPhone.startsWith("+84")) {
            // Keep international format as is
            return cleanPhone;
        }
        
        return phone; // Return original if no formatting applied
    }
}
