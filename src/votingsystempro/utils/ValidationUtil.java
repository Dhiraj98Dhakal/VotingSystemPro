package votingsystempro.utils;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Email validation pattern
    private static final String EMAIL_PATTERN = 
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" 
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    
    // Phone validation pattern (Nepal phone numbers)
    private static final String PHONE_PATTERN = 
        "^(\\+977[- ]?)?(98|97|96)[0-9]{8}$|^(0[1-9][0-9]{1,2})[-]?[0-9]{6,7}$";
    
    // Citizenship number pattern (Nepal citizenship)
    private static final String CITIZENSHIP_PATTERN = 
        "^[0-9]{1,2}-[0-9]{2}-[0-9]{2}[A-Za-z]?$|^[0-9]{5,10}$";
    
    // Name validation pattern (only letters and spaces)
    private static final String NAME_PATTERN = "^[a-zA-Z\\s]+$";
    
    // Voter ID pattern
    private static final String VOTER_ID_PATTERN = "^VOT[0-9]{13}$";
    
    /**
     * Validates email address
     * @param email email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Basic email validation
        email = email.trim().toLowerCase();
        
        // Check if email contains @ and .
        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }
        
        // Check if @ is not at start or end
        if (email.startsWith("@") || email.endsWith("@")) {
            return false;
        }
        
        // Check if dot is after @
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }
        
        String domain = parts[1];
        if (!domain.contains(".")) {
            return false;
        }
        
        // Check if dot is not at start or end of domain
        if (domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }
        
        // Common email providers validation
        String[] validDomains = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", 
                                 "live.com", "aol.com", "icloud.com", "mail.com", 
                                 "protonmail.com", "yopmail.com"};
        
        for (String validDomain : validDomains) {
            if (domain.equalsIgnoreCase(validDomain)) {
                return true;
            }
        }
        
        // If not in common domains, check pattern
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }
    
    /**
     * Validates phone number (Nepal format)
     * @param phone phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // Remove all spaces, dashes, and special characters
        phone = phone.trim().replaceAll("[\\s\\-()]", "");
        
        // Check if it's a mobile number (Nepal mobile: 98, 97, 96 followed by 8 digits)
        if (phone.matches("^(98|97|96)[0-9]{8}$")) {
            return true;
        }
        
        // Check if it's a landline with area code
        if (phone.matches("^0[1-9][0-9]{1,2}[0-9]{6,7}$")) {
            return true;
        }
        
        // Check if it includes country code +977
        if (phone.startsWith("+977")) {
            String withoutCode = phone.substring(4);
            return withoutCode.matches("^(98|97|96)[0-9]{8}$") || 
                   withoutCode.matches("^0[1-9][0-9]{1,2}[0-9]{6,7}$");
        }
        
        // Check if it's just 10 digits (simple validation)
        if (phone.matches("^[0-9]{10}$")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Validates Nepali citizenship number
     * @param citizenship citizenship number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCitizenship(String citizenship) {
        if (citizenship == null || citizenship.trim().isEmpty()) {
            return false;
        }
        
        citizenship = citizenship.trim().toUpperCase();
        
        // Format: 12-34-56 or 12-34-56A or 1234567890
        return Pattern.compile(CITIZENSHIP_PATTERN).matcher(citizenship).matches();
    }
    
    /**
     * Validates person name (only letters and spaces)
     * @param name name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        name = name.trim();
        
        // Check minimum length
        if (name.length() < 2) {
            return false;
        }
        
        // Check maximum length
        if (name.length() > 50) {
            return false;
        }
        
        // Check if contains only letters and spaces
        return Pattern.compile(NAME_PATTERN).matcher(name).matches();
    }
    
    /**
     * Validates voter ID format
     * @param voterId voter ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidVoterId(String voterId) {
        if (voterId == null || voterId.trim().isEmpty()) {
            return false;
        }
        
        return Pattern.compile(VOTER_ID_PATTERN).matcher(voterId).matches();
    }
    
    /**
     * Validates date of birth
     * @param day day of birth
     * @param month month of birth
     * @param year year of birth
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateOfBirth(int day, int month, int year) {
        // Basic range checks
        if (year < 1900 || year > 2024) {
            return false;
        }
        
        if (month < 1 || month > 12) {
            return false;
        }
        
        if (day < 1 || day > 31) {
            return false;
        }
        
        // Month-specific day checks
        if (month == 2) {
            // February
            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            if (isLeapYear && day > 29) {
                return false;
            } else if (!isLeapYear && day > 28) {
                return false;
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            // April, June, September, November have 30 days
            if (day > 30) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates password strength
     * @param password password to validate
     * @return true if password meets minimum requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Minimum length check
        if (password.length() < 6) {
            return false;
        }
        
        // Maximum length check
        if (password.length() > 20) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks password strength and returns feedback
     * @param password password to check
     * @return String with password strength feedback
     */
    public static String checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        
        StringBuilder feedback = new StringBuilder();
        
        if (password.length() < 6) {
            feedback.append("Password too short (minimum 6 characters). ");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            feedback.append("Add at least one uppercase letter. ");
        }
        
        if (!password.matches(".*[a-z].*")) {
            feedback.append("Add at least one lowercase letter. ");
        }
        
        if (!password.matches(".*[0-9].*")) {
            feedback.append("Add at least one number. ");
        }
        
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            feedback.append("Add at least one special character. ");
        }
        
        if (feedback.length() == 0) {
            return "Strong password";
        }
        
        return feedback.toString().trim();
    }
    
    /**
     * Validates if a string contains only numbers
     * @param str string to validate
     * @return true if string contains only numbers, false otherwise
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        return str.matches("\\d+");
    }
    
    /**
     * Validates if a string contains only alphanumeric characters
     * @param str string to validate
     * @return true if string contains only alphanumeric characters, false otherwise
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        return str.matches("[a-zA-Z0-9]+");
    }
    
    /**
     * Validates address (minimum requirements)
     * @param address address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        address = address.trim();
        
        // Minimum length
        if (address.length() < 5) {
            return false;
        }
        
        // Maximum length
        if (address.length() > 200) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Sanitizes input by removing dangerous characters
     * @param input input to sanitize
     * @return sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove SQL injection characters
        String sanitized = input.replaceAll("['\"\\\\;]", "");
        
        // Remove HTML tags
        sanitized = sanitized.replaceAll("<[^>]*>", "");
        
        return sanitized.trim();
    }
    
    /**
     * Validates age (must be at least 18)
     * @param age age to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAge(int age) {
        return age >= 18 && age <= 120;
    }
    
    /**
     * Validates if a string is empty or null
     * @param str string to check
     * @return true if empty or null, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validates file extension for images
     * @param fileName file name to check
     * @return true if valid image extension, false otherwise
     */
    public static boolean isValidImageFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        String extension = "";
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            extension = fileName.substring(lastDot + 1).toLowerCase();
        }
        
        return extension.equals("jpg") || extension.equals("jpeg") || 
               extension.equals("png") || extension.equals("gif");
    }
}