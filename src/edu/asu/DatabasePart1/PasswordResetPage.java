package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> PasswordResetPage. </p>
 * 
 * <p> Description: This class provides the user interface for resetting a user's password. 
 * The user is prompted to enter and confirm their new password, which is validated before 
 * being updated in the database. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 Password Reset Page
 * 
 */

public class PasswordResetPage {

    /** The primary stage used for the GUI interface */
    private final Stage primaryStage;
    
    /** The database helper that allows interactions with the user database */
    private final DatabaseHelper databaseHelper;
    
    /** The email of the user whose password is being reset */
    private final String email;
    
    /** The Grid Pane used to structure the password reset page UI */
    private final GridPane passwordResetGrid;

    /************
     * This constructor initializes the password reset page and sets up all of the 
     * components in the graphical interface, including buttons, labels, and password 
     * fields. It also manages the action for resetting the password and validates the 
     * entered data.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
     * @param databaseHelper	The database helper that enables interaction with the database
     * @param email				The email of the user who is resetting their password
     */
    public PasswordResetPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
    	
    	// Initializes the primaryStage, databaseHelper, and email
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup the layout for the password reset page using GridPane
        passwordResetGrid = new GridPane();
        passwordResetGrid.setAlignment(Pos.CENTER);
        passwordResetGrid.setVgap(10);
        passwordResetGrid.setHgap(10);

        // Define the labels, password fields, and buttons used in the user interface
        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();
        Label confirmNewPasswordLabel = new Label("Confirm New Password:");
        PasswordField confirmNewPasswordField = new PasswordField();
        Button resetPasswordButton = new Button("Reset Password");

        // Add components to the password reset grid layout
        passwordResetGrid.add(newPasswordLabel, 0, 0);
        passwordResetGrid.add(newPasswordField, 1, 0);
        passwordResetGrid.add(confirmNewPasswordLabel, 0, 1);
        passwordResetGrid.add(confirmNewPasswordField, 1, 1);
        passwordResetGrid.add(resetPasswordButton, 1, 2);

        // Adds functionality for the 'Reset Password' button
        resetPasswordButton.setOnAction(event -> {
        	// Collects the entered passwords
            String newPassword = newPasswordField.getText().trim();
            String confirmNewPassword = confirmNewPasswordField.getText().trim();

            // Validates that all fields are filled and that passwords match
            if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                showAlert("Error", "All fields must be filled.", Alert.AlertType.ERROR);
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
                return;
            }
            
            // Use PasswordChecker to validate the strength of the new password
            String validationMessage = PasswordChecker.evaluatePassword(newPassword);
            if (!validationMessage.isEmpty()) {
                showAlert("Password Validation Error", validationMessage, Alert.AlertType.ERROR);
                return;
            }

            // Attempts to reset the password in the database
            try {
                // Reset the user's password and remove the one-time password requirement
                databaseHelper.resetUserAccount(email, newPassword, null);
                
                // Show success message upon successful password reset
                showAlert("Success", "Password reset successfully. Please log in.", Alert.AlertType.INFORMATION);

                // Redirect to the login page after password reset
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                primaryStage.setScene(loginScene);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while resetting the password.", Alert.AlertType.ERROR);
            }
        });
    }

    // Method to return the password reset layout, used in the scene creation
    public GridPane getPasswordResetLayout() {
        return passwordResetGrid;
    }

    // Helper method to display alerts to the user
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
