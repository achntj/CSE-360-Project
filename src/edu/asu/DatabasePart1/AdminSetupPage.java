package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p>
 * AdminSetupPage.
 * </p>
 * 
 * <p>
 * Description: JavaFX interface implemented to set up admin users accounts in
 * the application. Allows users to enter various information about themselves
 * and adds to the database so the user is recognized again.
 * </p>
 * 
 * <p>
 * Copyright: Group 11 - CSE 360 © 2024
 * </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya
 *         Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Project Phase 1 Setting Up Admin account page
 * 
 */

public class AdminSetupPage {

	/** Primary stage used for the GUI Interface. */
	private final Stage primaryStage;

	/**
	 * Allows us to update and edit the database that holds all of the user
	 * information.
	 */
	private final DatabaseHelper databaseHelper;

	/** The Grid Pane used to map the admin set up page. */
	private final GridPane adminSetupGrid;

	public AdminSetupPage(Stage primaryStage, DatabaseHelper databaseHelper) {

		// Initializes the primaryStaged and database helper
		this.primaryStage = primaryStage;
		this.databaseHelper = databaseHelper;

		// Setup new admin registration UI Layout and alignment
		adminSetupGrid = new GridPane();
		adminSetupGrid.setAlignment(Pos.CENTER);
		adminSetupGrid.setVgap(10);
		adminSetupGrid.setHgap(10);

		// Establishes text and buttons to be used in user interface
		Label emailLabel = new Label("Email:");
		TextField emailField = new TextField();
		Label usernameLabel = new Label("Username:");
		TextField usernameField = new TextField();
		Label passwordLabel = new Label("Password:");
		PasswordField passwordField = new PasswordField();
		Label confirmPasswordLabel = new Label("Confirm Password:");
		PasswordField confirmPasswordField = new PasswordField();
		Label firstNameLabel = new Label("First Name:");
		TextField firstNameField = new TextField();
		Label lastNameLabel = new Label("Last Name:");
		TextField lastNameField = new TextField();
		Label preferredNameLabel = new Label("Preferred Name (Optional):");
		TextField preferredNameField = new TextField();
		Button createAdminButton = new Button("Create Admin Account");

		// Adds button and text components to the grid layout
		adminSetupGrid.add(emailLabel, 0, 0);
		adminSetupGrid.add(emailField, 1, 0);
		adminSetupGrid.add(usernameLabel, 0, 1);
		adminSetupGrid.add(usernameField, 1, 1);
		adminSetupGrid.add(passwordLabel, 0, 2);
		adminSetupGrid.add(passwordField, 1, 2);
		adminSetupGrid.add(confirmPasswordLabel, 0, 3);
		adminSetupGrid.add(confirmPasswordField, 1, 3);
		adminSetupGrid.add(firstNameLabel, 0, 4);
		adminSetupGrid.add(firstNameField, 1, 4);
		adminSetupGrid.add(lastNameLabel, 0, 5);
		adminSetupGrid.add(lastNameField, 1, 5);
		adminSetupGrid.add(preferredNameLabel, 0, 6);
		adminSetupGrid.add(preferredNameField, 1, 6);
		adminSetupGrid.add(createAdminButton, 1, 7);

		// Establishes functionality of create admin button
		createAdminButton.setOnAction(event -> {
			// Gathers new admin information from user
			String email = emailField.getText().trim();
			String username = usernameField.getText().trim();
			String password = passwordField.getText().trim();
			String confirmPassword = confirmPasswordField.getText().trim();
			String firstName = firstNameField.getText().trim();
			String lastName = lastNameField.getText().trim();
			String preferredName = preferredNameField.getText().trim();

			// Checks if any field is empty, if it is return an error
			if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
					|| firstName.isEmpty() || lastName.isEmpty()) {
				showAlert("Error", "All fields except Preferred Name must be filled.", Alert.AlertType.ERROR);
				return;
			}

			// Checks if password entered twice matches, and if they do not returns an error
			if (!password.equals(confirmPassword)) {
				showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
				return;
			}

			// Uses PasswordEvaluator to validate the password and returns an error if the
			// requirements are not met
			String validationMessage = PasswordChecker.evaluatePassword(password);
			if (!validationMessage.isEmpty()) {
				showAlert("Password Validation Error", validationMessage, Alert.AlertType.ERROR);
				return;
			}

			try {
				// Connects to the database
				databaseHelper.ensureConnection();

				// Register the first user as Admin and displays if addition was successful
				databaseHelper.register(email, username, password, "Admin", false, null, firstName, null, lastName,
						preferredName.isEmpty() ? null : preferredName, "beginner");
				showAlert("Success", "Admin account created successfully. Please log in.", Alert.AlertType.INFORMATION);

				// Redirect to new Login Page and passes in primaryStage and databaseHelper
				LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
				Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
				primaryStage.setScene(loginScene);

			} catch (SQLException e) {
				e.printStackTrace();
				showAlert("Database Error", "An error occurred while creating the admin account.",
						Alert.AlertType.ERROR);
			}
		});
	}

	// Get method for admin set up page layout
	public GridPane getAdminSetupLayout() {
		return adminSetupGrid;
	}

	// Helper method to show alerts to the user
	private void showAlert(String title, String content, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}
