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
 * <p>  Description: Setup for an interactive JavaFX page that allows users to send messages to the system. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-11-18 Project Phase 3 Help Message Page
 * 
 */

public class HelpMessagePage {

	/** Primary stage used for the GUI Interface */
    private final Stage primaryStage;
    
    /** Allows us to update and edit the database that holds all of the user information. */
    private final DatabaseHelper databaseHelper;
    
    /** The email of the logged-in user */
    private final String email;
    
    /** The role of the logged-in user */
    private final String role;
    
    /** The Grid Pane used to map the current user alter role page. */
    private final GridPane messageGrid;

    /************
     * This method initializes all of the elements used in the graphical interface presented for 
     * the HelpMessagePage. It sets up the alignment and text fields used  as well as manages the 
     * interactions with the page and handles errors that occur. 
     * 
     * @param primaryStage		Input of primaryStage used to manage the graphics changes
     * @param databaseHelper	Input of the databaseHelper that allows us to interact with the content of the database
     */
    public HelpMessagePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
    	
    	// Initializes the primaryStaged and database helper 
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Creates a new GridPane and sets the alignment
        messageGrid = new GridPane();
        messageGrid.setAlignment(Pos.CENTER);
        messageGrid.setVgap(10);
        messageGrid.setHgap(10);

        // Establishes text and buttons to be used in user interface
        TextField genericMessageField = new TextField();
        Button sendGeneric = new Button("Send Generic Message");
        TextField specificMessageField = new TextField();        
        Button sendSpecific = new Button("Send Specific Message");
        Button backButton = new Button("Back");

        // Adds the buttons and text fields to the user interface
        messageGrid.add(genericMessageField, 0, 0);
        messageGrid.add(sendGeneric, 1, 0);
        messageGrid.add(specificMessageField, 0, 1);
        messageGrid.add(sendSpecific, 1, 1);
        messageGrid.add(backButton, 0, 2);

        // Adds the functionality for add role button
        sendGeneric.setOnAction(event -> {
        	// Collects the message written by the user.
            String message = genericMessageField.getText().trim();

            // If the message is empty, displays error message and alerts user to provide one
            if (message.isEmpty()) {
                showAlert("Error", "Message must be provided.", Alert.AlertType.ERROR);
                return;
            }
            
            // Sends the message and alerts the current user if it went successfully
            try {
                databaseHelper.sendMessage(message, email, "generic");
                showAlert("Success", "Message sent successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while sending the message.", Alert.AlertType.ERROR);
            }
        });
        
        // Adds the functionality for add role button
        sendSpecific.setOnAction(event -> {
        	// Collects the message written by the user.
            String message = specificMessageField.getText().trim();

            // If the message is empty, displays error message and alerts user to provide one
            if (message.isEmpty()) {
                showAlert("Error", "Message must be provided.", Alert.AlertType.ERROR);
                return;
            }
            
            // Sends the message and alerts the current user if it went successfully
            try {
                databaseHelper.sendMessage(message, email, "specific");
                showAlert("Success", "Message sent successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while sending the message.", Alert.AlertType.ERROR);
            }
        });

        
        // Provides functionality for the back button
        backButton.setOnAction(event -> {
        	// Creates a new home with the primary stage and database helper and returns to the home scene
            UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, role);
            Scene userScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
            primaryStage.setScene(userScene);
        });
    }

    // Get function for messageGrid 
    public GridPane getHelpMessageLayout() {
        return messageGrid;
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

