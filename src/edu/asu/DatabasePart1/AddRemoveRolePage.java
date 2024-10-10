package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> AddRemoveRolePage. </p>
 * 
 * <p>  Description: Setup for an interactive JavaFX page that allows admin users to add 
 * or remove roles from specific users in the database </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 Add and Remove Role Page
 * 
 */

public class AddRemoveRolePage {

	/** Primary stage used for the GUI Interface */
    private final Stage primaryStage;
    
    /** Allows us to update and edit the database that holds all of the user information. */
    private final DatabaseHelper databaseHelper;
    
    /** The Grid Pane used to map the current user alter role page. */
    private final GridPane roleGrid;

    /************
     * This method initializes all of the elements used in the graphical interface presented for 
     * the AddRemoveRolePage. It sets up the alignment and text fields used  as well as manages the 
     * interactions with the page and handles errors that occur. 
     * 
     * @param primaryStage		Input of primaryStage used to manage the graphics changes
     * @param databaseHelper	Input of the databaseHelper that allows us to interact with the content of the database
     */
    public AddRemoveRolePage(Stage primaryStage, DatabaseHelper databaseHelper) {
    	
    	// Initializes the primaryStaged and database helper 
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Creates a new GridPane and sets the alignment
        roleGrid = new GridPane();
        roleGrid.setAlignment(Pos.CENTER);
        roleGrid.setVgap(10);
        roleGrid.setHgap(10);

        // Establishes text and buttons to be used in user interface
        Label emailLabel = new Label("User Email:");
        TextField emailField = new TextField();
        Label roleLabel = new Label("Role:");
        TextField roleField = new TextField();
        Button addRoleButton = new Button("Add Role");
        Button removeRoleButton = new Button("Remove Role");
        Button backButton = new Button("Back");

        // Adds the buttons and text fields to the user interface
        roleGrid.add(emailLabel, 0, 0);
        roleGrid.add(emailField, 1, 0);
        roleGrid.add(roleLabel, 0, 1);
        roleGrid.add(roleField, 1, 1);
        roleGrid.add(addRoleButton, 0, 2);
        roleGrid.add(removeRoleButton, 1, 2);
        roleGrid.add(backButton, 1, 3);

        // Adds the functionality for add role button
        addRoleButton.setOnAction(event -> {
        	// Collects which role to add and which email to add it to
            String email = emailField.getText().trim();
            String role = roleField.getText().trim();

            // If the role or email is empty, displays error message and alerts user to provide one
            if (email.isEmpty() || role.isEmpty()) {
                showAlert("Error", "Email and role must be provided.", Alert.AlertType.ERROR);
                return;
            }
            
            // Adds the role to the entered user and alerts the current user if it went successfully
            try {
                databaseHelper.addRoleToUser(email, role);
                showAlert("Success", "Role added successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while adding the role.", Alert.AlertType.ERROR);
            }
        });

        // Adds the functionality for remove role button
        removeRoleButton.setOnAction(event -> {
        	// Collects which role to add and which email to add it to
            String email = emailField.getText().trim();
            String role = roleField.getText().trim();

            // If the role or email is empty, displays error message and alerts user to provide one
            if (email.isEmpty() || role.isEmpty()) {
                showAlert("Error", "Email and role must be provided.", Alert.AlertType.ERROR);
                return;
            }

            // Removes the role from the entered user and alerts the current user if it went successfully
            try {
                databaseHelper.removeRoleFromUser(email, role);
                showAlert("Success", "Role removed successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while removing the role.", Alert.AlertType.ERROR);
            }
        });

        // Provides functionality for the back button
        backButton.setOnAction(event -> {
        	// Creates a new home with the primary stage and database helper and returns to the admin scene
            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper);
            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
            primaryStage.setScene(adminScene);
        });
    }

    // Get function for roleGrid 
    public GridPane getAddRemoveRoleLayout() {
        return roleGrid;
    }

    // Creates and displays pop up alert to user 
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

