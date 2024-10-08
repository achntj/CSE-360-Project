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

public class AdminSetupPage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final GridPane adminSetupGrid;

    public AdminSetupPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Setup Admin Registration UI Layout
        adminSetupGrid = new GridPane();
        adminSetupGrid.setAlignment(Pos.CENTER);
        adminSetupGrid.setVgap(10);
        adminSetupGrid.setHgap(10);

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

        // Add components to the grid
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

        // Create Admin Account Button Action
        createAdminButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String preferredName = preferredNameField.getText().trim();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                showAlert("Error", "All fields except Preferred Name must be filled.", Alert.AlertType.ERROR);
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
                return;
            }
            
            // Use PasswordEvaluator to validate the password
            String validationMessage = PasswordChecker.evaluatePassword(password);
            if (!validationMessage.isEmpty()) {
                showAlert("Password Validation Error", validationMessage, Alert.AlertType.ERROR);
                return;
            }

            try {
                databaseHelper.ensureConnection();
                // Register the first user as Admin
                databaseHelper.register(email, username, password, "Admin", false, null, firstName, null, lastName, preferredName.isEmpty() ? null : preferredName, "beginner");
                showAlert("Success", "Admin account created successfully. Please log in.", Alert.AlertType.INFORMATION);

                // Redirect to Login Page
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                primaryStage.setScene(loginScene);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while creating the admin account.", Alert.AlertType.ERROR);
            }
        });
    }

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
