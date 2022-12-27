package calendar.utils;

import java.util.regex.Pattern;

public class InputValidation {

    /**
     * Checks if an email is in a valid format.
     * @param email - The email we wish to validate.
     * @return - true or false, valid or not.
     */
    public static boolean isValidEmail(String email) {

        String regexPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        if (email != null && patternMatches(email, regexPattern)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a name is in a valid format.
     * @param name - The email we wish to validate.
     * @return - true or false, valid or not.
     */
    public static boolean isValidName(String name) {

        String regexPattern = "[ a-zA-Z]{3,30}";                   // only letters. length: 3-30
        if (name != null && patternMatches(name, regexPattern)) {

            return true;
        }

        return false;
    }

    /**
     * Checks if a password is in a valid format.
     * @param password - The email we wish to validate.
     * @return - true or false, valid or not.
     */
    public static boolean isValidPassword(String password) {

        // Minimum eight characters, at least one letter and one number:
        String regexPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

        if (password != null && !password.equals("") && patternMatches(password, regexPattern)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a string matches a pattern.
     * @param fieldToValidate - What we want to validate.
     * @param regexPattern - The pattern we want to validate againts.
     * @return true or false, valid or not.
     */
    private static boolean patternMatches(String fieldToValidate, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(fieldToValidate)
                .matches();
    }
}
