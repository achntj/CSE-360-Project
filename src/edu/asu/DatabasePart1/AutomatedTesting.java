package edu.asu.DatabasePart1;

/*******
 * <p>
 * PasswordEvaluationTestingAutomation Class
 * </p>
 * 
 * <p>
 * Description: A class responsible for automating the testing of the
 * PasswordChecker class.
 * </p>
 * 
 * <p>
 * Copyright: Group 11 - CSE 360 © 2024
 * </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya
 *         Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Documented code and added more robust test cases
 * 
 */

public class AutomatedTesting {
	// Variables to track the number of passed and failed test cases
	static int numPassed = 0;
	static int numFailed = 0;

	/**
	 * Main method to execute the testing automation. This method initiates the test
	 * cases and prints the final results.
	 * 
	 * @param args Command line arguments (not used in this application).
	 */
	public static void main(String[] args) {
		// Print the header for the testing automation
		System.out.println("____________________________________________________________________________");
		System.out.println("\nTesting Automation");

		// Execute test cases with sample inputs and expected outcomes

		// Testing valid passwords
		passwordTestCase(1, "Aa!15678", true); // Valid password
		passwordTestCase(2, "Abcdef1!", true); // Valid password with different characters
		passwordTestCase(3, "Passw0rd@", true); // Valid password with upper, lower, digit, special
		passwordTestCase(4, "StrongP@ss1", true); // Longer valid password

		// Testing invalid passwords
		passwordTestCase(5, "A!", false); // Too short
		passwordTestCase(6, "abcdefg", false); // No uppercase, digit, or special character
		passwordTestCase(7, "ABCDEFG", false); // No lowercase, digit, or special character
		passwordTestCase(8, "12345678", false); // No letters or special character
		passwordTestCase(9, "Password1", false); // Missing special character
		passwordTestCase(10, "", false);

		articleTestCase(11, "Introduction to Databases", "Beginner", "John Doe", "An introductory guide to databases.",
				"database,sql,introduction", "This article covers basics of SQL and database management.", "Ref1, Ref2",
				true);

		// Test case 2: Missing title, should fail
		articleTestCase(12, "", "Intermediate", "Jane Doe", "A guide on optimizing database queries.",
				"database,optimization", "This article explores ways to optimize SQL queries.", "Ref1, Ref2", false);

		// Test case 3: Valid article with detailed content, should pass
		articleTestCase(13, "Advanced Database Techniques", "Advanced", "Alice Smith",
				"Detailed insights into database optimization.", "database,optimization,advanced",
				"This article delves into complex SQL query optimization and indexing strategies.", "Ref3, Ref4, Ref5",
				true);

		// Test case 4: Missing authors, should fail
		articleTestCase(14, "Database Security", "Intermediate", "", "An overview of database security practices.",
				"database,security", "This article covers best practices for database security.", "Ref6", false);

		// Test case 5: Empty abstract, should fail
		articleTestCase(15, "NoSQL Databases", "Beginner", "Tom White", "", "NoSQL,database",
				"This article explains NoSQL databases and their use cases.", "Ref7", false);

		// Print final results of test execution
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	/**
	 * Performs a test case to evaluate a password against expected results. This
	 * method checks the input password against the validation rules defined in the
	 * PasswordChecker class and compares the actual outcome with the expected
	 * result.
	 * 
	 * @param testCase     The test case number for identification.
	 * @param inputText    The password input to be evaluated.
	 * @param expectedPass Indicates if the test case is expected to pass (true) or
	 *                     fail (false).
	 */
	private static void passwordTestCase(int testCase, String inputText, boolean expectedPass) {
		// Print details of the current test case being executed
		System.out.println("____________________________________________________________________________\n\nTest case: "
				+ testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");

		// Evaluate the password using the PasswordChecker class
		String resultText = PasswordChecker.evaluatePassword(inputText);

		System.out.println();

		// Set the result based on the evaluation result
		if (resultText != "") {
			// Failure case: password is invalid but expected to be valid
			if (expectedPass) {
				System.out.println("***Failure*** The password <" + inputText + "> is invalid."
						+ "\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				// Increment the failed tests count
				numFailed++;
			}
			// Success case: password is invalid and expected to be invalid
			else {
				System.out.println("***Success*** The password <" + inputText + "> is invalid."
						+ "\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				// Increment the passed tests count
				numPassed++;
			}
		}

		else {
			// Success case: password is valid and expected to be valid
			if (expectedPass) {
				System.out.println("***Success*** The password <" + inputText + "> is valid, so this is a pass!");
				// Increment the passed tests count
				numPassed++;
			}
			// Failure case: password is valid but expected to be invalid
			else {
				System.out.println("***Failure*** The password <" + inputText + "> was judged as valid"
						+ "\nBut it was supposed to be invalid, so this is a failure!");
				// Increment the failed tests count
				numFailed++;
			}
		}
		displayArticleEvaluation();
	}

	private static void articleTestCase(int testCase, String title, String difficulty, String authors,
			String abstractText, String keywords, String body, String references, boolean expectedPass) {
		System.out.println("____________________________________________________________________________\n\nTest case: "
				+ testCase);
		System.out.println("");

		String resultText = ArticleFunctionChecker.addedAndDeleted(title, difficulty, authors, abstractText, keywords,
				body, references);

		System.out.println();

		// Set the result based on the evaluation result
		if (resultText != "") {
			// Failure case: password is invalid but expected to be valid
			if (expectedPass) {
				System.out.println("***Failure***" + "\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				// Increment the failed tests count
				numFailed++;
			}
			// Success case: password is invalid and expected to be invalid
			else {
				System.out.println("***Success***" + "\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				// Increment the passed tests count
				numPassed++;
			}
		}

		else {
			// Success case: password is valid and expected to be valid
			if (expectedPass) {
				System.out.println("***Success*** the article was added, so this is a pass!");
				// Increment the passed tests count
				numPassed++;
			}
			// Failure case: password is valid but expected to be invalid
			else {
				System.out.println("***Failure*** " + "\nBut it was supposed to be invalid, so this is a failure!");
				// Increment the failed tests count
				numFailed++;
			}
		}
		displayPasswordEvaluation();

	}

	/**
	 * Displays the evaluation results of the password criteria. This method checks
	 * if each password requirement was satisfied and prints the result.
	 */
	private static void displayPasswordEvaluation() {

		// Check if at least one upper case letter is found
		if (PasswordChecker.foundUpperCase)
			System.out.println("At least one upper case letter - Satisfied");
		else
			System.out.println("At least one upper case letter - Not Satisfied");

		// Check if at least one lower case letter is found
		if (PasswordChecker.foundLowerCase)
			System.out.println("At least one lower case letter - Satisfied");
		else
			System.out.println("At least one lower case letter - Not Satisfied");

		// Check if at least one numeric digit is found
		if (PasswordChecker.foundNumericDigit)
			System.out.println("At least one digit - Satisfied");
		else
			System.out.println("At least one digit - Not Satisfied");

		// Check if at least one special character is found
		if (PasswordChecker.foundSpecialChar)
			System.out.println("At least one special character - Satisfied");
		else
			System.out.println("At least one special character - Not Satisfied");

		// Check if the password is long enough (at least 8 characters)
		if (PasswordChecker.foundLongEnough)
			System.out.println("At least 8 characters - Satisfied");
		else
			System.out.println("At least 8 characters - Not Satisfied");

	}

	private static void displayArticleEvaluation() {

		if (ArticleFunctionChecker.articleAdded) {
			System.out.println("Article was added - satisfied");
		} else {
			System.out.println("Article was not added - Not Satisfied");
		}

		if (ArticleFunctionChecker.articleDeleted) {
			System.out.println("Article was deleted - satisfied");
		} else {
			System.out.println("Article was not deleted - Not Satisfied");
		}
	}
}
