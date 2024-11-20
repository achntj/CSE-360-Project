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
        Button createGroupButton = new Button("Create General Group");
        Button createSpecialAccessGroupButton = new Button("Create Special Access Group");
        
        Button listGroupsButton = new Button("        List Groups        ");
        
        Label groupIDLabel = new Label("Group ID: ");
        TextField groupField = new TextField();
        
        Button viewGroupButton = new Button("View Group");   
        Button deleteGroupButton = new Button("Delete Group");
        
        Button backupGroupButton = new Button("Backup Group");   
        Button restoreGroupButton = new Button("Restore Group");
        
        Button helpButton = new Button("Help");
        Button backButton = new Button("Back");

        // Adds the buttons and text fields to the user interface
        
        groupGrid.add(createGroupButton, 0, 0);
        groupGrid.add(createSpecialAccessGroupButton, 2, 0);
        
        groupGrid.add(listGroupsButton, 0, 1);
 
        groupGrid.add(groupIDLabel, 0, 2);    
        groupGrid.add(groupField, 2, 2);
        
        groupGrid.add(viewGroupButton, 0, 3);    
        groupGrid.add(deleteGroupButton, 2, 3);
        
        groupGrid.add(backupGroupButton, 0, 4);
        groupGrid.add(restoreGroupButton, 2, 4); 
        
        groupGrid.add(helpButton, 0, 5);    
        groupGrid.add(backButton, 2, 5);
        
        createGroupButton.setOnAction(event -> {
        	System.out.println("createGeneralGroup Button pressed");
        });

        createSpecialAccessGroupButton.setOnAction(event -> {
        	try {
                databaseHelper.ensureConnection();
                
                // Redirect to create group page
                CreateSpecialGroupPage createSpecialGroupPage = new CreateSpecialGroupPage(primaryStage, databaseHelper, email, role);
                Scene createGroupScene = new Scene(createSpecialGroupPage.getCreateGroupLayout(), 400, 300);
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

        viewGroupButton.setOnAction(event -> {
        	System.out.println("viewGroupButton  pressed");
        });
        deleteGroupButton.setOnAction(event -> {
        	System.out.println("deleteGroupButton  pressed");
        });
        backupGroupButton.setOnAction(event -> {
        	System.out.println("backupGroupButton  pressed");
        });
        restoreGroupButton.setOnAction(event -> {
        	System.out.println("restoreGroupButton  pressed");
        });

		helpButton.setOnAction(event -> {
			System.out.println("helpButton pressed");
		});
        
        // Provides functionality for the back button
        backButton.setOnAction(event -> {
        	
        	if (role.equalsIgnoreCase("admin")) {                   
                AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
                Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
                primaryStage.setScene(adminScene);
            }
            
            else if (role.equalsIgnoreCase("instructor")) {                   
                InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper, email, role);
                Scene instructorScene = new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300);
                primaryStage.setScene(instructorScene);
            }    	 
            else {
           
           	 try {
                    // Ensure connection to the database and log the user out
                    databaseHelper.ensureConnection();
                    showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);

                    // Redirect to the login page after logout
                    LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                    Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                    primaryStage.setScene(loginScene);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "An error occurred during logout.", Alert.AlertType.ERROR);
                }
            }   
     
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

