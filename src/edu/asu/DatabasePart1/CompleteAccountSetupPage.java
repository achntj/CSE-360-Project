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

/**
 * <p> CompleteAccountSetupPage. </p>
 * 
 * <p>  Description: Page of JavaFX implemented application that allows users to complete 
 * the set up of their accounts and adds the information gathered to the database. </p>
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 finish setting up account page
 * 
 */

public class CompleteAccountSetupPage {

	/** Primary stage used for the GUI Interface. */
    private final Stage primaryStage;
    
    /** Allows us to update and edit the database that holds all of the user information. */
    private final DatabaseHelper databaseHelper;
    
    /** Holds the email of the user which we are setting up. */
    private final String email;
    
    /** The Grid Pane used to map the complete setup page. */
    private final GridPane completeSetupGrid;
    
    public CompleteAccountSetupPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
        
    	// Initializes the passed in variables 
    	this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup Complete Account and Setup UI Layout and Alignment
        completeSetupGrid = new GridPane();
        completeSetupGrid.setAlignment(Pos.CENTER);
        completeSetupGrid.setVgap(10);
        completeSetupGrid.setHgap(10);

        // Initializes buttons and text fields for the UI
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
        Label githubLevelLabel = new Label("Github Level: ");
        TextField githubLevelField = new TextField();
        Button completeSetupButton = new Button("Complete Setup");

        // Adds initialized buttons, text fields, and labels to interface 
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

        // Establishes functionality of complete set up 
        completeSetupButton.setOnAction(event -> {
        	
        	// Gathers information from user to pass into database
            String firstName = firstNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String preferredName = preferredNameField.getText().trim();
            String javaLevel = javaLevelField.getText().trim();
            String javaFXLevel = javaFXLevelField.getText().trim();
            String githubLevel = githubLevelField.getText().trim();

            // Checks if user passed in any empty values and returns an error if they did
            if (firstName.isEmpty() || lastName.isEmpty() || javaLevel.isEmpty() || javaFXLevel.isEmpty() || githubLevel.isEmpty()) {
                showAlert("Error", "First Name, Last Name, or Level fields cannot be empty.", Alert.AlertType.ERROR);
                return;
            }

            try {
                // Update user information in the database
                databaseHelper.updateUserAccount(email, firstName, middleName, lastName, preferredName, javaLevel, javaFXLevel, githubLevel);
                showAlert("Success", "Account setup completed successfully.", Alert.AlertType.INFORMATION);
                
                // Redirects the user to new User Role Selection Page
                RoleSelectionPage roleSelectionPage = new RoleSelectionPage(primaryStage, databaseHelper, email);
                Scene roleSelectionScene = new Scene(roleSelectionPage.getRoleSelectionLayout(), 400, 300);
                primaryStage.setScene(roleSelectionScene);
                        
                
                // Displays error if there was an issue adding the account
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while updating the account.", Alert.AlertType.ERROR);
            }
        });
    }

    //Getter Method for the layout of Complete Set Up Page
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

