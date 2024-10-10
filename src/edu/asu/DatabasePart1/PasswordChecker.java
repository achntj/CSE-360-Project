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
    
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	// We need to define these variables globally (public) for testing access
	// The names of the variables specify their function and each is initialize as required
	public static boolean foundUpperCase;
	public static boolean foundLowerCase;
	public static boolean foundNumericDigit;
	public static boolean foundSpecialChar;
	public static boolean foundLongEnough;


	/**********
	 * This is the method that evaluates each password character by character
	 */
    public static String evaluatePassword(String password) {
    	// We set checks to false by default for each string
    	foundUpperCase = false;
    	foundLowerCase = false;
    	foundNumericDigit = false;
    	foundSpecialChar = false;
    	foundLongEnough = false;


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
            errMessage += "Upper case, ";
        
        if (!foundLowerCase)
            errMessage += "Lower case, ";
        
        if (!foundNumericDigit)
            errMessage += "Numeric digits, ";
        
        if (!foundSpecialChar)
            errMessage += "Special character, ";
        
        if (!foundLongEnough)
            errMessage += "At least 8 characters, ";

        // If no criteria were violated, return an empty string (indicating valid password)
        if (errMessage.isEmpty())
            return "";

        // Return the error message
        return errMessage + "conditions were not satisfied.";
    }
}
