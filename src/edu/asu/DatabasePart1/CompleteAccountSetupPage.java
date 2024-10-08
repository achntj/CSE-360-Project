package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CompleteAccountSetupPage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final String email;
    private final GridPane completeSetupGrid;

    public CompleteAccountSetupPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup Complete Account Setup UI Layout
        completeSetupGrid = new GridPane();
        completeSetupGrid.setAlignment(Pos.CENTER);
        completeSetupGrid.setVgap(10);
        completeSetupGrid.setHgap(10);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        Label middleNameLabel = new Label("Middle Name:");
        TextField middleNameField = new TextField();
        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        Label preferredNameLabel = new Label("Preferred Name (Optional):");
        TextField preferredNameField = new TextField();
        Label javaLevelLabel = new Label("Java Level:");
        TextField javaLevelField = new TextField();
        Label javaFXLevelLabel = new Label("JavaFX Level: ");
        TextField javaFXLevelField = new TextField();
        Label githubLevelLabel = new Label("JavaFX Level: ");
        TextField githubLevelField = new TextField();
        Button completeSetupButton = new Button("Complete Setup");

        completeSetupGrid.add(firstNameLabel, 0, 0);
        completeSetupGrid.add(firstNameField, 1, 0);
        completeSetupGrid.add(middleNameLabel, 0, 1);
        completeSetupGrid.add(middleNameField, 1, 1);
        completeSetupGrid.add(lastNameLabel, 0, 2);
        completeSetupGrid.add(lastNameField, 1, 2);
        completeSetupGrid.add(preferredNameLabel, 0, 3);
        completeSetupGrid.add(preferredNameField, 1, 3);
        completeSetupGrid.add(javaLevelLabel, 0, 4);
        completeSetupGrid.add(javaLevelField, 1, 4);
        completeSetupGrid.add(javaFXLevelLabel, 0, 5);
        completeSetupGrid.add(javaFXLevelField, 1, 5);
        completeSetupGrid.add(githubLevelLabel, 0, 6);
        completeSetupGrid.add(githubLevelField, 1, 6);
        completeSetupGrid.add(completeSetupButton, 1, 7);

        // Complete Setup Button Action
        completeSetupButton.setOnAction(event -> {
            String firstName = firstNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String preferredName = preferredNameField.getText().trim();
            String javaLevel = javaLevelField.getText().trim();
            String javaFXLevel = javaFXLevelField.getText().trim();
            String githubLevel = githubLevelField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || javaLevel.isEmpty() || javaFXLevel.isEmpty() || githubLevel.isEmpty()) {
                showAlert("Error", "First Name, Last Name, or Level fields cannot be empty.", Alert.AlertType.ERROR);
                return;
            }

            try {
                // Update user information in the database
                databaseHelper.updateUserAccount(email, firstName, middleName, lastName, preferredName, javaLevel, javaFXLevel, githubLevel);
                showAlert("Success", "Account setup completed successfully.", Alert.AlertType.INFORMATION);

                
                //Redirect to User Role Selection Page
                RoleSelectionPage roleSelectionPage = new RoleSelectionPage(primaryStage, databaseHelper, email);
                Scene roleSelectionScene = new Scene(roleSelectionPage.getRoleSelectionLayout(), 400, 300);
                primaryStage.setScene(roleSelectionScene);
                        
                
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while updating the account.", Alert.AlertType.ERROR);
            }
        });
    }

    public GridPane getCompleteSetupLayout() {
        return completeSetupGrid;
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

