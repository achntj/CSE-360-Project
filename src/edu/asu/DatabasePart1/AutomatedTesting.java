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
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel,
 *         Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Documented code and added more robust test cases
 */

public class AutomatedTesting {
	// Variables to track the number of passed and failed test cases
	static int numPassed = 0;
	static int numFailed = 0;

	/**
	 * Main method to execute the testing automation. Initializes and runs test
	 * cases, then prints the final results.
	 * 
	 * @param args Command line arguments (not used in this application).
	 */
	public static void main(String[] args) {
		// Print header for testing output
		System.out.println("____________________________________________________________________________");
		System.out.println("\nTesting Automation");

		// Run test cases for valid passwords
		passwordTestCase(1, "Aa!15678", true); // Meets all criteria
		passwordTestCase(2, "Abcdef1!", true); // Valid with different characters
		passwordTestCase(3, "Passw0rd@", true); // Valid with upper, lower, digit, special
		passwordTestCase(4, "StrongP@ss1", true); // Valid longer password

		// Run test cases for invalid passwords
		passwordTestCase(5, "A!", false); // Too short
		passwordTestCase(6, "abcdefg", false); // No uppercase, digit, or special
		passwordTestCase(7, "ABCDEFG", false); // No lowercase, digit, or special
		passwordTestCase(8, "12345678", false); // No letters or special character
		passwordTestCase(9, "Password1", false); // Missing special character
		passwordTestCase(10, "", false); // Empty password

		// Run article test cases for validation
		articleTestCase(11, "Introduction to Databases", "Beginner", "John Doe", 
		                "An introductory guide to databases.", "database,sql,introduction", 
		                "This article covers basics of SQL and database management.", 
		                "Ref1, Ref2", true); // Valid article

		articleTestCase(12, "", "Intermediate", "Jane Doe", 
		                "A guide on optimizing database queries.", 
		                "database,optimization", "This article explores ways to optimize SQL queries.", 
		                "Ref1, Ref2", false); // Missing title, expected to fail

		articleTestCase(13, "Advanced Database Techniques", "Advanced", "Alice Smith",
		                "Detailed insights into database optimization.", "database,optimization,advanced",
		                "This article delves into complex SQL query optimization and indexing strategies.", 
		                "Ref3, Ref4, Ref5", true); // Valid with detailed content

		articleTestCase(14, "Database Security", "Intermediate", "", 
		                "An overview of database security practices.", 
		                "database,security", "This article covers best practices for database security.", 
		                "Ref6", false); // Missing authors, expected to fail

		articleTestCase(15, "NoSQL Databases", "Beginner", "Tom White", "", 
		                "NoSQL,database", "This article explains NoSQL databases and their use cases.", 
		                "Ref7", false); // Empty abstract, expected to fail

		// Print final results of test execution
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: " + numPassed);
		System.out.println("Number of tests failed: " + numFailed);
	}

	/**
	 * Evaluates a password against expected results using the PasswordChecker class.
	 * Compares actual result with expected outcome and updates pass/fail counts.
	 * 
	 * @param testCase     Identifier for the test case.
	 * @param inputText    Password input to be evaluated.
	 * @param expectedPass Expected outcome (true for pass, false for fail).
	 */
	private static void passwordTestCase(int testCase, String inputText, boolean expectedPass) {
		System.out.println("____________________________________________________________________________\n\nTest case: "
				+ testCase);
		System.out.println("Input: \"" + inputText + "\"");
		System.out.println("______________");
		System.out.println("\nFinite state machine execution trace:");

		String resultText = PasswordChecker.evaluatePassword(inputText);

		System.out.println();

		if (!resultText.isEmpty()) {
			if (expectedPass) {
				System.out.println("***Failure*** Expected valid, found invalid.");
				System.out.println("Error message: " + resultText);
				numFailed++;
			} else {
				System.out.println("***Success*** Expected invalid, found invalid.");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		} else {
			if (expectedPass) {
				System.out.println("***Success*** Expected valid, found valid.");
				numPassed++;
			} else {
				System.out.println("***Failure*** Expected invalid, found valid.");
				numFailed++;
			}
		}
		displayArticleEvaluation();
	}

	/**
	 * Executes an article test case for validation, comparing the actual outcome
	 * with the expected result.
	 * 
	 * @param testCase     Identifier for the test case.
	 * @param title        Title of the article.
	 * @param difficulty   Difficulty level.
	 * @param authors      Article authors.
	 * @param abstractText Abstract text of the article.
	 * @param keywords     Associated keywords.
	 * @param body         Content of the article.
	 * @param references   References cited in the article.
	 * @param expectedPass Expected outcome (true for pass, false for fail).
	 */
	private static void articleTestCase(int testCase, String title, String difficulty, String authors,
			String abstractText, String keywords, String body, String references, boolean expectedPass) {
		System.out.println("____________________________________________________________________________\n\nTest case: "
				+ testCase);
		System.out.println("");

		String resultText = ArticleFunctionChecker.addedAndDeleted(title, difficulty, authors, abstractText, keywords,
				body, references);

		System.out.println();

		if (!resultText.isEmpty()) {
			if (expectedPass) {
				System.out.println("***Failure*** Expected valid, found invalid.");
				System.out.println("Error message: " + resultText);
				numFailed++;
			} else {
				System.out.println("***Success*** Expected invalid, found invalid.");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		} else {
			if (expectedPass) {
				System.out.println("***Success*** Article added, expected valid.");
				numPassed++;
			} else {
				System.out.println("***Failure*** Expected invalid, found valid.");
				numFailed++;
			}
		}
		displayPasswordEvaluation();
	}

	/**
	 * Displays results of the password evaluation criteria.
	 */
	private static void displayPasswordEvaluation() {
		if (PasswordChecker.foundUpperCase)
			System.out.println("Upper case letter requirement - Satisfied");
		else
			System.out.println("Upper case letter requirement - Not Satisfied");

		if (PasswordChecker.foundLowerCase)
			System.out.println("Lower case letter requirement - Satisfied");
		else
			System.out.println("Lower case letter requirement - Not Satisfied");

		if (PasswordChecker.foundNumericDigit)
			System.out.println("Digit requirement - Satisfied");
		else
			System.out.println("Digit requirement - Not Satisfied");

		if (PasswordChecker.foundSpecialChar)
			System.out.println("Special character requirement - Satisfied");
		else
			System.out.println("Special character requirement - Not Satisfied");

		if (PasswordChecker.foundLongEnough)
			System.out.println("Length requirement - Satisfied");
		else
			System.out.println("Length requirement - Not Satisfied");
	}

	/**
	 * Displays the evaluation of article addition and deletion results.
	 */
	private static void displayArticleEvaluation() {
		if (ArticleFunctionChecker.articleAdded) {
			System.out.println("Article addition - Satisfied");
		} else {
			System.out.println("Article addition - Not Satisfied");
		}

		if (ArticleFunctionChecker.articleDeleted) {
			System.out.println("Article deletion - Satisfied");
		} else {
			System.out.println("Article deletion - Not Satisfied");
		}
	}
}
