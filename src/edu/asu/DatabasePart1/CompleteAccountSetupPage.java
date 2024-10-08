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
        Button completeSetupButton = new Button("Complete Setup");

        completeSetupGrid.add(firstNameLabel, 0, 0);
        completeSetupGrid.add(firstNameField, 1, 0);
        completeSetupGrid.add(middleNameLabel, 0, 1);
        completeSetupGrid.add(middleNameField, 1, 1);
        completeSetupGrid.add(lastNameLabel, 0, 2);
        completeSetupGrid.add(lastNameField, 1, 2);
        completeSetupGrid.add(preferredNameLabel, 0, 3);
        completeSetupGrid.add(preferredNameField, 1, 3);
        completeSetupGrid.add(completeSetupButton, 1, 4);

        // Complete Setup Button Action
        completeSetupButton.setOnAction(event -> {
            String firstName = firstNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String preferredName = preferredNameField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                showAlert("Error", "First Name and Last Name fields cannot be empty.", Alert.AlertType.ERROR);
                return;
            }

            try {
                // Update user information in the database
                databaseHelper.updateUserAccount(email, firstName, middleName, lastName, preferredName);
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

