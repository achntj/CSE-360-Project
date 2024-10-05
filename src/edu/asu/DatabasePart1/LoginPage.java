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

public class LoginPage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final GridPane loginGrid;

    public LoginPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Setup Login UI Layout
        loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setVgap(10);
        loginGrid.setHgap(10);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginGrid.add(emailLabel, 0, 0);
        loginGrid.add(emailField, 1, 0);
        loginGrid.add(passwordLabel, 0, 1);
        loginGrid.add(passwordField, 1, 1);
        loginGrid.add(loginButton, 1, 2);
        loginGrid.add(registerButton, 1, 3);

        // Login Button Action
        loginButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            // Validate input fields
            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Email and Password fields cannot be empty.", Alert.AlertType.ERROR);
                return;
            }

            try {
                databaseHelper.ensureConnection(); // Ensure the connection is established

                // Check login credentials
                if (databaseHelper.login(email, password)) {
                    if (databaseHelper.isOneTimePassword(email)) {
                        System.out.println("User needs to reset their password.");

                        // Redirect to the password reset page
                        PasswordResetPage passwordResetPage = new PasswordResetPage(primaryStage, databaseHelper, email);
                        Scene resetScene = new Scene(passwordResetPage.getPasswordResetLayout(), 400, 300);
                        primaryStage.setScene(resetScene);

                    } else if (!databaseHelper.isAccountSetupComplete(email)) {
                        System.out.println("User needs to complete account setup.");

                        // Redirect to the complete account setup page
                        CompleteAccountSetupPage setupPage = new CompleteAccountSetupPage(primaryStage, databaseHelper, email);
                        Scene setupScene = new Scene(setupPage.getCompleteSetupLayout(), 400, 300);
                        primaryStage.setScene(setupScene);

                    } else {
                        System.out.println("Login successful");

                        // Check if the user is an admin
                        if (databaseHelper.hasRole(email, "Admin")) {
                            // Redirect to admin home page
                            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper);
                            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
                            primaryStage.setScene(adminScene);
                        }
                        // Check if the user has multiple roles
                        else if (databaseHelper.hasMultipleRoles(email)) {
                            // Redirect to role selection page
                            RoleSelectionPage roleSelectionPage = new RoleSelectionPage(primaryStage, databaseHelper, email);
                            Scene roleScene = new Scene(roleSelectionPage.getRoleSelectionLayout(), 400, 300);
                            primaryStage.setScene(roleScene);
                        } else {
                        	// Redirect to the user home page
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

        // Register Button Action
        registerButton.setOnAction(event -> {
            RegisterPage registerPage = new RegisterPage(primaryStage, databaseHelper);
            Scene registerScene = new Scene(registerPage.getRegisterLayout(), 400, 400);
            primaryStage.setScene(registerScene);
        });
    }

    // Method to get the login layout for use in the scene
    public GridPane getLoginLayout() {
        return loginGrid;
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
