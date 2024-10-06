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

public class SetNewPassword {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final String email;
    private final GridPane passwordResetGrid;

    public SetNewPassword(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup Password Reset UI Layout
        passwordResetGrid = new GridPane();
        passwordResetGrid.setAlignment(Pos.CENTER);
        passwordResetGrid.setVgap(10);
        passwordResetGrid.setHgap(10);

        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();
        Label confirmNewPasswordLabel = new Label("Confirm New Password:");
        PasswordField confirmNewPasswordField = new PasswordField();
        Button resetPasswordButton = new Button("Reset Password");

        passwordResetGrid.add(newPasswordLabel, 0, 0);
        passwordResetGrid.add(newPasswordField, 1, 0);
        passwordResetGrid.add(confirmNewPasswordLabel, 0, 1);
        passwordResetGrid.add(confirmNewPasswordField, 1, 1);
        passwordResetGrid.add(resetPasswordButton, 1, 2);

        // Reset Password Button Action
        resetPasswordButton.setOnAction(event -> {
            String newPassword = newPasswordField.getText().trim();
            String confirmNewPassword = confirmNewPasswordField.getText().trim();

            if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                showAlert("Error", "All fields must be filled.", Alert.AlertType.ERROR);
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
                return;
            }

            try {
                // Set expiry to null as OTP is no longer required after password reset
                databaseHelper.setNewPassword(email, newPassword);
                
                // Show success message
                showAlert("Success", "Password reset successfully. Please log in.", Alert.AlertType.INFORMATION);

                // Redirect to Login Page
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                primaryStage.setScene(loginScene);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while resetting the password.", Alert.AlertType.ERROR);
            }
        });
    }

    public GridPane getPasswordResetLayout() {
        return passwordResetGrid;
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

