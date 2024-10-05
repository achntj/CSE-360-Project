package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class DeleteUserAccountPage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final GridPane deleteUserGrid;

    public DeleteUserAccountPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        deleteUserGrid = new GridPane();
        deleteUserGrid.setAlignment(Pos.CENTER);
        deleteUserGrid.setVgap(10);
        deleteUserGrid.setHgap(10);

        Label emailLabel = new Label("User Email:");
        TextField emailField = new TextField();
        Button deleteUserButton = new Button("Delete User Account");
        Button backButton = new Button("Back");

        deleteUserGrid.add(emailLabel, 0, 0);
        deleteUserGrid.add(emailField, 1, 0);
        deleteUserGrid.add(deleteUserButton, 1, 1);
        deleteUserGrid.add(backButton, 1, 2);

        // Delete User Account Button Action
        deleteUserButton.setOnAction(event -> {
            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                showAlert("Error", "Email must be provided.", Alert.AlertType.ERROR);
                return;
            }

            // Confirm deletion
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this user?", ButtonType.YES, ButtonType.NO);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText(null);
            confirmation.showAndWait();

            if (confirmation.getResult() == ButtonType.YES) {
                try {
                    databaseHelper.deleteUserAccount(email);
                    showAlert("Success", "User account deleted successfully.", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Database Error", "An error occurred while deleting the user account.", Alert.AlertType.ERROR);
                }
            }
        });

        // Back Button Action
        backButton.setOnAction(event -> {
            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper);
            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
            primaryStage.setScene(adminScene);
        });
    }

    public GridPane getDeleteUserLayout() {
        return deleteUserGrid;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

