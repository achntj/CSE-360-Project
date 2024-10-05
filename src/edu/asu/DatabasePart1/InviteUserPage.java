package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class InviteUserPage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final GridPane inviteUserGrid;

    public InviteUserPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        inviteUserGrid = new GridPane();
        inviteUserGrid.setAlignment(Pos.CENTER);
        inviteUserGrid.setVgap(10);
        inviteUserGrid.setHgap(10);

        Label roleLabel = new Label("Role(s):");
        TextField roleField = new TextField();
        Button generateCodeButton = new Button("Generate Invitation Code");
        Button backButton = new Button("Back");

        inviteUserGrid.add(roleLabel, 0, 0);
        inviteUserGrid.add(roleField, 1, 0);
        inviteUserGrid.add(generateCodeButton, 1, 1);
        inviteUserGrid.add(backButton, 1, 2);

        // Generate Invitation Code Button Action
        generateCodeButton.setOnAction(event -> {
            String roles = roleField.getText().trim();

            if (roles.isEmpty()) {
                showAlert("Error", "Role(s) must be specified.", Alert.AlertType.ERROR);
                return;
            }

            try {
                String invitationCode = databaseHelper.generateInvitationCode(roles);
                showAlert("Success", "Invitation code generated: " + invitationCode, Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while generating the invitation code.", Alert.AlertType.ERROR);
            }
        });

        // Back Button Action
        backButton.setOnAction(event -> {
            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper);
            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
            primaryStage.setScene(adminScene);
        });
    }

    public GridPane getInviteUserLayout() {
        return inviteUserGrid;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

