package com.example.smartzone.helpers

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class PasswordValidatorTests {
    @Test
    fun passwordTooShort_returnsFalse() {
        assertFalse(PasswordValidator.isValid("Ab1"))
    }

    @Test
    fun passwordNoUppercase_returnsFalse() {
        assertFalse(PasswordValidator.isValid("password1"))
    }

    @Test
    fun passwordNoLowercase_returnsFalse() {
        assertFalse(PasswordValidator.isValid("PASSWORD1"))
    }

    @Test
    fun passwordNoDigit_returnsFalse() {
        assertFalse(PasswordValidator.isValid("Password"))
    }

    @Test
    fun validPassword_returnsTrue() {
        assertTrue(PasswordValidator.isValid("Passw0rd"))
    }
}