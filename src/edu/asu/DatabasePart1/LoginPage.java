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
 * <p> LoginPage. </p>
 * 
 * <p> Description: This class provides the user interface for the login page, 
 * allowing users to enter their email and password to log in, and handles the 
 * redirection based on user roles and account status. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 Login Page
 * 
 */

public class LoginPage {

    /** The primary stage used for the GUI interface */
    private final Stage primaryStage;
    
    /** The database helper that allows interactions with the user database */
    private final DatabaseHelper databaseHelper;
    
    /** The Grid Pane used to structure the login page UI */
    private final GridPane loginGrid;

    /************
     * This constructor initializes the login page and sets up all of the 
     * components in the graphical interface, including buttons, labels, 
     * and text fields. It also manages button actions for logging in 
     * and registering.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
     * @param databaseHelper	The database helper that enables interaction with the database
     */
    public LoginPage(Stage primaryStage, DatabaseHelper databaseHelper) {
    	
    	// Initializes the primaryStage and database helper
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Setup the login page layout using GridPane and configure its alignment
        loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setVgap(10);
        loginGrid.setHgap(10);

        // Define the labels, text fields, and buttons used in the user interface
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        // Add components to the login grid layout
        loginGrid.add(emailLabel, 0, 0);
        loginGrid.add(emailField, 1, 0);
        loginGrid.add(passwordLabel, 0, 1);
        loginGrid.add(passwordField, 1, 1);
        loginGrid.add(loginButton, 1, 2);
        loginGrid.add(registerButton, 1, 3);

        // Adds functionality for the 'Login' button
        loginButton.setOnAction(event -> {
        	// Collects the entered email and password
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            // Validates that both fields are filled, shows an error if not
            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Email and Password fields cannot be empty.", Alert.AlertType.ERROR);
                return;
            }

            try {
                // Ensures that a connection to the database is established
                databaseHelper.ensureConnection();

                // Verifies the login credentials
                if (databaseHelper.login(email, password)) {
                    if (databaseHelper.isOneTimePassword(email)) {
                        System.out.println("User needs to reset their password.");

                        // Redirects to the password reset page
                        SetNewPassword passwordResetPage = new SetNewPassword(primaryStage, databaseHelper, email);
                        Scene resetScene = new Scene(passwordResetPage.getPasswordResetLayout(), 400, 300);
                        primaryStage.setScene(resetScene);

                    } else if (!databaseHelper.isAccountSetupComplete(email)) {
                        System.out.println("User needs to complete account setup.");

                        // Redirects to the account setup page
                        CompleteAccountSetupPage setupPage = new CompleteAccountSetupPage(primaryStage, databaseHelper, email);
                        Scene setupScene = new Scene(setupPage.getCompleteSetupLayout(), 400, 300);
                        primaryStage.setScene(setupScene);

                    } else {
                        System.out.println("Login successful");

                        // Check if the user is an Admin, and redirect accordingly
                        if (databaseHelper.hasRole(email, "Admin")) {
                            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper);
                            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
                            primaryStage.setScene(adminScene);
                        }
                        // Check if the user has multiple roles
                        else if (databaseHelper.hasMultipleRoles(email)) {
                            RoleSelectionPage roleSelectionPage = new RoleSelectionPage(primaryStage, databaseHelper, email);
                            Scene roleScene = new Scene(roleSelectionPage.getRoleSelectionLayout(), 400, 300);
                            primaryStage.setScene(roleScene);
                        } else {
                            // Redirect to the user home page based on the user's role
                            String role = databaseHelper.getUserRole(email);
                            UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, role);
                            Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
                            primaryStage.setScene(userHomeScene);
                        }
                    }
                } else {
                    showAlert("Login Failed", "Invalid email or password. Please try again.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while accessing the database.", Alert.AlertType.ERROR);
            }
        });

        // Adds functionality for the 'Register' button to redirect to the registration page
        registerButton.setOnAction(event -> {
            RegisterPage registerPage = new RegisterPage(primaryStage, databaseHelper);
            Scene registerScene = new Scene(registerPage.getRegisterLayout(), 400, 400);
            primaryStage.setScene(registerScene);
        });
    }

    // Method to return the login layout, used in the scene creation
    public GridPane getLoginLayout() {
        return loginGrid;
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
