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
 * <p> SetNewPassword. </p>
 * 
 * <p> Description: This class provides the user interface for setting a new
 * password after an initial login or password reset requirement. It validates
 * the inputted password and updates the user's account in the database. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 * @version 2.00 2024-10-30 Updated for Phase 2
 * @version 3.00 2024-11-20 Updated for Phase 3
 */
public class SetNewPassword {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The email of the user who is setting a new password. */
    private final String email;

    /** The GridPane used to structure the set new password page UI. */
    private final GridPane passwordResetGrid;

    /**
     * Constructs the SetNewPassword page with the given parameters.
     * Initializes the set new password page and sets up all components in the 
     * graphical interface, including password fields for entering and confirming 
     * the new password, and a button to confirm the password reset.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the user who is setting a new password
     */
    public SetNewPassword(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup the layout for the set new password page using GridPane
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

        // Add functionality for the 'Reset Password' button
        resetPasswordButton.setOnAction(event -> {
            handleResetPassword(newPasswordField, confirmNewPasswordField);
        });
    }

    /**
     * Handles the password reset process, including validation and database update.
     * 
     * @param newPasswordField       the field for entering the new password
     * @param confirmNewPasswordField the field for confirming the new password
     */
    private void handleResetPassword(PasswordField newPasswordField, PasswordField confirmNewPasswordField) {
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

        // Attempts to set the new password in the database
        try {
            databaseHelper.setNewPassword(email, newPassword);

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
    }

    /**
     * Returns the password reset layout, used in scene creation.
     * 
     * @return the GridPane layout of the Set New Password Page
     */
    public GridPane getPasswordResetLayout() {
        return passwordResetGrid;
    }

    /**
     * Displays an alert with the specified title, content, and alert type.
     * 
     * @param title     the title of the alert
     * @param content   the content of the alert
     * @param alertType the type of alert (e.g., ERROR, INFORMATION)
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}