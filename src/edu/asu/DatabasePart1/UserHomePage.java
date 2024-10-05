package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class UserHomePage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final String email;
    private final String role;
    private final GridPane homeGrid;

    public UserHomePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup User Home UI Layout
        homeGrid = new GridPane();
        homeGrid.setAlignment(Pos.CENTER);
        homeGrid.setVgap(10);
        homeGrid.setHgap(10);

        Label welcomeLabel = new Label("Welcome to User Home Page, " + role + "!");
        Button logoutButton = new Button("Log Out");

        homeGrid.add(welcomeLabel, 0, 0);
        homeGrid.add(logoutButton, 0, 1);

        // Logout Button Action
        logoutButton.setOnAction(event -> {
            try {
                databaseHelper.ensureConnection();
                showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);

                // Redirect to Login Page
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                primaryStage.setScene(loginScene);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred during logout.", Alert.AlertType.ERROR);
            }
        });
    }

    // Method to get the user home layout for use in the scene
    public GridPane getUserHomeLayout() {
        return homeGrid;
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

