package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Timestamp;

public class ResetUserAccountPage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final GridPane resetUserGrid;

    public ResetUserAccountPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        resetUserGrid = new GridPane();
        resetUserGrid.setAlignment(Pos.CENTER);
        resetUserGrid.setVgap(10);
        resetUserGrid.setHgap(10);

        Label emailLabel = new Label("User Email:");
        TextField emailField = new TextField();
        Label newPasswordLabel = new Label("New Password:");
        PasswordField newPasswordField = new PasswordField();
        Label expiryLabel = new Label("Expiry (yyyy-MM-dd HH:mm:ss):");
        TextField expiryField = new TextField();
        Button resetPasswordButton = new Button("Reset Password");
        Button backButton = new Button("Back");

        resetUserGrid.add(emailLabel, 0, 0);
        resetUserGrid.add(emailField, 1, 0);
        resetUserGrid.add(newPasswordLabel, 0, 1);
        resetUserGrid.add(newPasswordField, 1, 1);
        resetUserGrid.add(expiryLabel, 0, 2);
        resetUserGrid.add(expiryField, 1, 2);
        resetUserGrid.add(resetPasswordButton, 1, 3);
        resetUserGrid.add(backButton, 1, 4);

        // Reset Password Button Action
        resetPasswordButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String expiryText = expiryField.getText().trim();

            if (email.isEmpty() || newPassword.isEmpty() || expiryText.isEmpty()) {
                showAlert("Error", "All fields must be filled.", Alert.AlertType.ERROR);
                return;
            }

            try {
                Timestamp expiry = Timestamp.valueOf(expiryText);
                databaseHelper.resetUserAccount(email, newPassword, expiry);
                showAlert("Success", "User password reset successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException | IllegalArgumentException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while resetting the user account.", Alert.AlertType.ERROR);
            }
        });

        // Back Button Action
        backButton.setOnAction(event -> {
            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper);
            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
            primaryStage.setScene(adminScene);
        });
    }

    public GridPane getResetUserLayout() {
        return resetUserGrid;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

