package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * <p> ResetUserAccountPage. </p>
 * 
 * <p> Description: This class provides the user interface for resetting a user's
 * password and updating the expiry date for their account. The admin inputs the
 * user's email, a new password, and an expiry timestamp. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * <p> This page is accessible only to administrators. </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 * @version 2.00 2024-10-30 Updated for Phase 2
 * @version 3.00 2024-11-20 Updated for Phase 3
 */
public class ResetUserAccountPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in admin. */
    private final String email;

    /** The GridPane used to structure the reset user account page UI. */
    private final GridPane resetUserGrid;

    /**
     * Constructs the ResetUserAccountPage with the given parameters.
     * Initializes the reset user account page and sets up all components in the 
     * graphical interface, including buttons, labels, and input fields for 
     * resetting a user's password and account expiry date. It also manages the 
     * actions for resetting the password and returning to the admin home page.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in admin
     */
    public ResetUserAccountPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup the layout for the reset user account page using GridPane
        resetUserGrid = new GridPane();
        resetUserGrid.setAlignment(Pos.CENTER);
        resetUserGrid.setVgap(10);
        resetUserGrid.setHgap(10);

        // Define the labels, input fields, and buttons used in the user interface
        Label emailLabel = new Label("User Email:");
        TextField emailField = new TextField();
        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();
        Label expiryLabel = new Label("Expiry (yyyy-MM-dd HH:mm:ss):");
        TextField expiryField = new TextField();
        Button resetPasswordButton = new Button("Reset Password");
        Button backButton = new Button("Back");

        // Add components to the reset user account grid layout
        resetUserGrid.add(emailLabel, 0, 0);
        resetUserGrid.add(emailField, 1, 0);
        resetUserGrid.add(newPasswordLabel, 0, 1);
        resetUserGrid.add(newPasswordField, 1, 1);
        resetUserGrid.add(expiryLabel, 0, 2);
        resetUserGrid.add(expiryField, 1, 2);
        resetUserGrid.add(resetPasswordButton, 1, 3);
        resetUserGrid.add(backButton, 1, 4);

        // Adds functionality for the 'Reset Password' button
        resetPasswordButton.setOnAction(event -> {
            // Collects the entered data from the form
            String userEmail = emailField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String expiryText = expiryField.getText().trim();

            // Validates that all fields are filled
            if (userEmail.isEmpty() || newPassword.isEmpty() || expiryText.isEmpty()) {
                showAlert("Error", "All fields must be filled.", Alert.AlertType.ERROR);
                return;
            }

            // Attempts to reset the user's password and expiry date
            try {
                Timestamp expiry = Timestamp.valueOf(expiryText);
                databaseHelper.resetUserAccount(userEmail, newPassword, expiry);
                showAlert("Success", "User password reset successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException | IllegalArgumentException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while resetting the user account.", Alert.AlertType.ERROR);
            }
        });

        // Adds functionality for the 'Back' button to return to the admin home page
        backButton.setOnAction(event -> {
            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
            primaryStage.setScene(adminScene);
        });
    }

    /**
     * Returns the reset user account layout, used in scene creation.
     * 
     * @return the GridPane layout of the Reset User Account Page
     */
    public GridPane getResetUserLayout() {
        return resetUserGrid;
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