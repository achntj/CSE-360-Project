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
 * <p> Description: This class provides the user interface for resetting a user's password 
 * and updating the expiry date for their account. The admin inputs the user's email, a new 
 * password, and an expiry timestamp. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 Reset User Account Page
 * 
 */

public class ResetUserAccountPage {

    /** The primary stage used for the GUI interface */
    private final Stage primaryStage;
    
    /** The database helper that allows interactions with the user database */
    private final DatabaseHelper databaseHelper;
    
    /** The email of the logged-in user */
    private final String email;
    
    /** The Grid Pane used to structure the reset user account page UI */
    private final GridPane resetUserGrid;

    /************
     * This constructor initializes the reset user account page and sets up all of the 
     * components in the graphical interface, including buttons, labels, and input fields 
     * for resetting a user's password and account expiry date. It also manages the actions 
     * for resetting the password and returning to the admin home page.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
     * @param databaseHelper	The database helper that enables interaction with the database
     */
    public ResetUserAccountPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
    	
    	// Initializes the primaryStage and database helper
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

    // Method to return the reset user account layout, used in the scene creation
    public GridPane getResetUserLayout() {
        return resetUserGrid;
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
