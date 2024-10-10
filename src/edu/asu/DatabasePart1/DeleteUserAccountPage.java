package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> DeleteUserAccountPage. </p>
 * 
 * <p>  Description: Interactive JavaFX page that allows admin users 
 * to remove accounts from the database. 
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 Delete User Account Page
 * 
 */

public class DeleteUserAccountPage {

	/** Primary stage used for the GUI Interface */
    private final Stage primaryStage;
    
    /** Allows us to update and edit the database that holds all of the user information. */
    private final DatabaseHelper databaseHelper;
    
    /** The Grid Pane used to determine the layout of UI */
    private final GridPane deleteUserGrid;

   
    /************
     * This method initializes all of the elements used in the graphical interface presented for 
     * the DeleteUserAccountPage. It sets up the alignment and text fields used  as well as manages the 
     * interactions with the page and handles errors that occur. 
     * 
     * @param primaryStage		Input of primaryStage used to manage the graphics changes
     * @param databaseHelper	Input of the databaseHelper that allows us to interact with the content of the database
     */
    
    public DeleteUserAccountPage(Stage primaryStage, DatabaseHelper databaseHelper) {
    	
    	// Initializes the primaryStaged and database helper
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Creates a new GridPane and sets the alignment
        deleteUserGrid = new GridPane();
        deleteUserGrid.setAlignment(Pos.CENTER);
        deleteUserGrid.setVgap(10);
        deleteUserGrid.setHgap(10);

        // Establishes text and buttons to be used in user interface
        Label emailLabel = new Label("User Email:");
        TextField emailField = new TextField();
        Button deleteUserButton = new Button("Delete User Account");
        Button backButton = new Button("Back");

        // Adds the buttons and text fields to the user interface
        deleteUserGrid.add(emailLabel, 0, 0);
        deleteUserGrid.add(emailField, 1, 0);
        deleteUserGrid.add(deleteUserButton, 1, 1);
        deleteUserGrid.add(backButton, 1, 2);

        // Adds Functionality for Delete User Account Button Action
        deleteUserButton.setOnAction(event -> {
        	
        	// gets an email associated with an account to remove
            String email = emailField.getText().trim();

            // Checks if the email provided is empty 
            if (email.isEmpty()) {
                showAlert("Error", "Email must be provided.", Alert.AlertType.ERROR);
                return;
            }

            // Confirm deletion popup 
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this user?", ButtonType.YES, ButtonType.NO);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText(null);
            confirmation.showAndWait();

            //Accesses the database to remove the account associated with the email
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

        // Returns to a new AdminHomePage
        backButton.setOnAction(event -> {
            AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper);
            Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
            primaryStage.setScene(adminScene);
        });
    }

    //Get Method for grid layout of delete user page
    public GridPane getDeleteUserLayout() {
        return deleteUserGrid;
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

