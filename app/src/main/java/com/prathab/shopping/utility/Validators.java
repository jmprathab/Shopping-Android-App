package com.prathab.shopping.utility;

/**
 * Utility class which contains static methods for Mobile, Email and Password Validation
 */
public class Validators {

  public static boolean isEmailValid(String input) {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
  }

  public static boolean isMobileValid(String input) {
    return android.util.Patterns.PHONE.matcher(input).matches();
  }

  public static boolean isPasswordValid(String input) {
    return !input.isEmpty();
  }
}
