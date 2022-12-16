package calendar.utils;

import java.util.regex.Pattern;

public class InputValidation {
    public enum Field {NAME, EMAIL, PASSWORD}
    public Field field;

    public static boolean isValidEmail(String email) {
        String regexPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (email != null && patternMatches(email, regexPattern)) {
            return true;
        }
        return false;
    }

    public static boolean isValidName(String name) {
        String regexPattern = "[ a-zA-Z]{3,30}";                   // only letters. length: 3-30
        if (name != null && patternMatches(name, regexPattern)) {
            return true;
        }
        return false;
    }

    public static boolean isValidPassword(String password) {
        // Minimum eight characters, at least one letter and one number:
        String regexPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (password != null && patternMatches(password, regexPattern)) {
            return true;
        }
        return false;
    }

    private static boolean patternMatches(String fieldToValidate, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(fieldToValidate)
                .matches();
    }
}
