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

public class RegisterPage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final GridPane registerGrid;

    public RegisterPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Setup Register UI Layout
        registerGrid = new GridPane();
        registerGrid.setAlignment(Pos.CENTER);
        registerGrid.setVgap(10);
        registerGrid.setHgap(10);

        Label invitationCodeLabel = new Label("Invitation Code:");
        TextField invitationCodeField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        Button createAccountButton = new Button("Create Account");
        Button backToLoginButton = new Button("Back to Login");

        registerGrid.add(invitationCodeLabel, 0, 0);
        registerGrid.add(invitationCodeField, 1, 0);
        registerGrid.add(emailLabel, 0, 1);
        registerGrid.add(emailField, 1, 1);
        registerGrid.add(usernameLabel, 0, 2);
        registerGrid.add(usernameField, 1, 2);
        registerGrid.add(passwordLabel, 0, 3);
        registerGrid.add(passwordField, 1, 3);
        registerGrid.add(confirmPasswordLabel, 0, 4);
        registerGrid.add(confirmPasswordField, 1, 4);
        registerGrid.add(createAccountButton, 1, 5);
        registerGrid.add(backToLoginButton, 1, 6);

        // Create Account Button Action
        createAccountButton.setOnAction(event -> {
            String invitationCode = invitationCodeField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            // Validate input fields
            if (invitationCode.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("Error", "All fields must be filled.", Alert.AlertType.ERROR);
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
                return;
            }

            try {
                databaseHelper.ensureConnection();

                // Register the user with the invitation code and email
                boolean registrationSuccessful = databaseHelper.registerWithInvitationCode(invitationCode, username, password);
                if (registrationSuccessful) {
                    // Update email field for the user
                    databaseHelper.updateUserEmail(username, email);
                    showAlert("Success", "Account created successfully. Please log in.", Alert.AlertType.INFORMATION);

                    // Redirect to Login Page
                    LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                    Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                    primaryStage.setScene(loginScene);
                } else {
                    showAlert("Error", "Invalid invitation code. Please check and try again.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while creating the account.", Alert.AlertType.ERROR);
            }
        });

        // Back to Login Button Action
        backToLoginButton.setOnAction(event -> {
            LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
            Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
            primaryStage.setScene(loginScene);
        });
    }

    public GridPane getRegisterLayout() {
        return registerGrid;
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
