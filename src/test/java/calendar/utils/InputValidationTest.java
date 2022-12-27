package calendar.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidationTest {

    private static String email;

    private static String password;

    private static String name;

    @BeforeEach
    void setup() {
        email = "leon@test.com";

        password = "leon1234";

        name = "leon";
    }

    @Test
    void isValidEmail_True() {
        assertTrue(InputValidation.isValidEmail(email));
    }

    @Test
    void isValidEmail_False() {
        email = "1234";
        assertFalse(InputValidation.isValidEmail(email));
    }

    @Test
    void isValidName_True() {
        assertTrue(InputValidation.isValidName(name));
    }

    @Test
    void isValidName_False() {
        name = "1234";
        assertFalse(InputValidation.isValidPassword(name));
    }

    @Test
    void isValidPassword_True() {
        assertTrue(InputValidation.isValidPassword(password));
    }

    @Test
    void isValidPassword_False() {
        password = "1234";
        assertFalse(InputValidation.isValidName(password));
    }
}