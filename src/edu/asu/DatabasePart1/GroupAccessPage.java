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

public class GroupAccessPage {

	/** Primary stage used for the GUI Interface */
    private final Stage primaryStage;
    
    /** Allows us to update and edit the database that holds all of the user information. */
    private final DatabaseHelper databaseHelper;
    
    /** The id of the logged-in user */
    private final String user_id;
    
    /** The email of the logged-in user */
    private final String email;
    
    /** The role of the logged-in user */
    private final String role;
  
    
    /** The Grid Pane used to map the current user alter role page. */
    private final GridPane groupGrid;

    /************
     * This method initializes all of the elements used in the graphical interface presented for 
     * the HelpMessagePage. It sets up the alignment and text fields used  as well as manages the 
     * interactions with the page and handles errors that occur. 
     * 
     * @param primaryStage		Input of primaryStage used to manage the graphics changes
     * @param databaseHelper	Input of the databaseHelper that allows us to interact with the content of the database
     */
    public GroupAccessPage(Stage primaryStage, DatabaseHelper databaseHelper, String user_id, String email, String role) {
    	
    	// Initializes the primaryStaged and database helper 
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.user_id = user_id;
        this.email = email;
        this.role = role;

        // Creates a new GridPane and sets the alignment
        groupGrid = new GridPane();
        groupGrid.setAlignment(Pos.CENTER);
        groupGrid.setVgap(10);
        groupGrid.setHgap(10);

        // Establishes text and buttons to be used in user interface
        Button createGroupButton = new Button("Create Group (use ID)");
        Button listGroupsButton = new Button("List Groups");
        TextField deleteGroupField = new TextField();
        Button deleteGroupButton = new Button("Delete Group (use ID)");
        Button backButton = new Button("Back");

        // Adds the buttons and text fields to the user interface
        
        groupGrid.add(createGroupButton, 0, 0);
        groupGrid.add(listGroupsButton, 0, 1);
        groupGrid.add(deleteGroupField, 0, 2);
        groupGrid.add(deleteGroupButton, 1, 2);
        groupGrid.add(backButton, 0, 3);

        createGroupButton.setOnAction(event -> {
        	try {
                databaseHelper.ensureConnection();
                
                // Redirect to create group page
                CreateGroupPage createGroupPage = new CreateGroupPage(primaryStage, databaseHelper, email, role);
                Scene createGroupScene = new Scene(createGroupPage.getCreateGroupLayout(), 400, 300);
                primaryStage.setScene(createGroupScene);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while entering the create group page.", Alert.AlertType.ERROR);
            }
        });

        listGroupsButton.setOnAction(event -> {
            try {
                // Retrieve the list of groups from the database
                String groupList = databaseHelper.listGroups();
                showAlert("Groups", groupList, Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while listing groups.", Alert.AlertType.ERROR);
            }
        });

        deleteGroupButton.setOnAction(event -> {
        	try {
        		String groupID = deleteGroupField.getText().trim();
                int id = Integer.parseInt(groupID);
                databaseHelper.deleteGroup(id);
                showAlert("Success", "Article deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid ID.", Alert.AlertType.ERROR);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while deleting the group.", Alert.AlertType.ERROR);
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

    // Get function for groupGrid 
    public GridPane getGroupAccessLayout() {
        return groupGrid;
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

