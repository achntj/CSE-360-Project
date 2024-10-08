package edu.asu.DatabasePart1;

public class PasswordChecker {

    public static String evaluatePassword(String password) {
        boolean foundUpperCase = false;
        boolean foundLowerCase = false;
        boolean foundNumericDigit = false;
        boolean foundSpecialChar = false;
        boolean foundLongEnough = false;

        // Check for each character in the password
        for (int i = 0; i < password.length(); i++) {
            char currentChar = password.charAt(i);

            // Check for uppercase letters
            if (currentChar >= 'A' && currentChar <= 'Z') {
                foundUpperCase = true;
            } 
            // Check for lowercase letters
            else if (currentChar >= 'a' && currentChar <= 'z') {
                foundLowerCase = true;
            } 
            // Check for numeric digits
            else if (currentChar >= '0' && currentChar <= '9') {
                foundNumericDigit = true;
            } 
            // Check for special characters
            else if ("!@#$%^&*()-_=+[]{}|;:'\",.<>?/".indexOf(currentChar) >= 0) {
                foundSpecialChar = true;
            } 
            // If an invalid character is found, return an error
            else {
                return "*** Error *** An invalid character has been found!";
            }
        }

        // Check if the password is long enough (at least 8 characters)
        if (password.length() >= 8) {
            foundLongEnough = true;
        }

        // Prepare an error message if any criteria are not met
        String errMessage = "";
        if (!foundUpperCase)
            errMessage += "Upper case; ";
        
        if (!foundLowerCase)
            errMessage += "Lower case; ";
        
        if (!foundNumericDigit)
            errMessage += "Numeric digits; ";
        
        if (!foundSpecialChar)
            errMessage += "Special character; ";
        
        if (!foundLongEnough)
            errMessage += "At least 8 characters; ";

        // If no criteria were violated, return an empty string (indicating valid password)
        if (errMessage.isEmpty())
            return "";

        // Return the error message
        return errMessage + "conditions were not satisfied.";
    }
}
