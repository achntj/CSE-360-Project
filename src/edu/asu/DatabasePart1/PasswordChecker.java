package edu.asu.DatabasePart1;

/*******
 * <p> PasswordEvaluator Class </p>
 * 
 * <p> Description: A class responsible for evaluating the validity of a password based on specific rules. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00	2024-10-09 Documented code and added more robust test cases
 * 
 */


public class PasswordChecker {

    /************
     * This method evaluates the given password based on multiple criteria including 
     * upper and lower case letters, numeric digits, special characters, and a minimum 
     * length of 8 characters. If any of the criteria are not satisfied, a message is 
     * returned specifying the missing requirements.
     * 
     * @param password		The input password to be evaluated
     * @return				An empty string if all criteria are satisfied, otherwise an 
     * 						error message listing the missing requirements
     */
	
	// Flags to track whether each criteria is met
	// The names of the variables specify their function and each is initialize as required
	public static boolean foundUpperCase = false;
	public static boolean foundLowerCase = false;
	public static boolean foundNumericDigit = false;
	public static boolean foundSpecialChar = false;
	public static boolean foundLongEnough = false;
	
    public static String evaluatePassword(String password) {
    	// We set flags to false by default for each string every time
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
