package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AddRemoveRolePage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final GridPane roleGrid;

    public AddRemoveRolePage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        roleGrid = new GridPane();
        roleGrid.setAlignment(Pos.CENTER);
        roleGrid.setVgap(10);
        roleGrid.setHgap(10);

        Label emailLabel = new Label("User Email:");
        TextField emailField = new TextField();
        Label roleLabel = new Label("Role:");
        TextField roleField = new TextField();
        Button addRoleButton = new Button("Add Role");
        Button removeRoleButton = new Button("Remove Role");
        Button backButton = new Button("Back");

        roleGrid.add(emailLabel, 0, 0);
        roleGrid.add(emailField, 1, 0);
        roleGrid.add(roleLabel, 0, 1);
        roleGrid.add(roleField, 1, 1);
        roleGrid.add(addRoleButton, 0, 2);
        roleGrid.add(removeRoleButton, 1, 2);
        roleGrid.add(backButton, 1, 3);

        // Add Role Button Action
        addRoleButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String role = roleField.getText().trim();

            if (email.isEmpty() || role.isEmpty()) {
                showAlert("Error", "Email and role must be provided.", Alert.AlertType.ERROR);
                return;
            }

            try {
                databaseHelper.addRoleToUser(email, role);
                showAlert("Success", "Role added successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while adding the role.", Alert.AlertType.ERROR);
            }
        });

        // Remove Role Button Action
        removeRoleButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String role = roleField.getText().trim();

            if (email.isEmpty() || role.isEmpty()) {
                showAlert("Error", "Email and role must be provided.", Alert.AlertType.ERROR);
                return;
            }

            try {
                databaseHelper.removeRoleFromUser(email, role);
                showAlert("Success", "Role removed successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while removing the role.", Alert.AlertType.ERROR);
            }
        });

        // Back Button Action
        backButton.setOnAction(event -> {
            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper);
            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
            primaryStage.setScene(adminScene);
        });
    }

    public GridPane getAddRemoveRoleLayout() {
        return roleGrid;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

