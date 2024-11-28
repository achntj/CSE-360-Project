package edu.asu.DatabasePart1;

/**
 * <p> PasswordChecker Class. </p>
 * 
 * <p> Description: This class evaluates the validity of a password based on 
 * specific rules. It checks for the presence of uppercase and lowercase 
 * letters, numeric digits, special characters, and a minimum length of 8 
 * characters. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * <p> This class is used to ensure passwords meet security requirements during 
 * user registration and password reset processes. </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version with Documentation and Test Cases
 */
public class PasswordChecker {

    /**
     * Evaluates the given password based on multiple criteria, including:
     * <ul>
     * <li>At least one uppercase letter</li>
     * <li>At least one lowercase letter</li>
     * <li>At least one numeric digit</li>
     * <li>At least one special character</li>
     * <li>Minimum length of 8 characters</li>
     * </ul>
     * If any of these criteria are not satisfied, the method returns a message
     * listing the missing requirements. If all criteria are satisfied, an empty 
     * string is returned.
     * 
     * @param password the input password to be evaluated
     * @return an empty string if all criteria are satisfied, or an error message
     *         listing the missing requirements
     */
	
	public static boolean foundUpperCase;
	public static boolean foundLowerCase;
	public static boolean foundNumericDigit;
	public static boolean foundSpecialChar;
	public static boolean foundLongEnough;
    
    public static String evaluatePassword(String password) {
        // Initialize flags to track whether each criterion is met
        boolean foundUpperCase = false;
        boolean foundLowerCase = false;
        boolean foundNumericDigit = false;
        boolean foundSpecialChar = false;
        boolean foundLongEnough = false;

        // Loop through each character in the password to check conditions
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
            // If an invalid character is found, return an error message
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
            errMessage += "Upper case, ";

        if (!foundLowerCase)
            errMessage += "Lower case, ";

        if (!foundNumericDigit)
            errMessage += "Numeric digits, ";

        if (!foundSpecialChar)
            errMessage += "Special character, ";

        if (!foundLongEnough)
            errMessage += "At least 8 characters, ";

        // If no criteria were violated, return an empty string indicating a valid password
        if (errMessage.isEmpty())
            return "";

        // Return the error message indicating which conditions were not satisfied
        return errMessage + "conditions were not satisfied.";
    }
}