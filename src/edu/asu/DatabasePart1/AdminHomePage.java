package edu.asu.DatabasePart1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> AdminHomePage. </p>
 * 
 * <p>  Description: Setup for interactive JavaFX user homepage for Admin users to interact with different aspects
 * of the database and aspects of the application.  </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00	2024-10-09 Project Phase 1 Admin Home Page
 */

public class AdminHomePage {

	/** Primary stage used for the GUI Interface */
    private final Stage primaryStage;
    
    /** Allows us to update and edit the database that holds all of the user information. */
    private final DatabaseHelper databaseHelper;
    
    /** The email of the logged-in user */
    private final String email;
    
    /** The Grid Pane used to map the current admin home page. */
    private final GridPane adminHomeGrid;

    /************
     * This method initializes all of the elements used in the graphical interface presented for 
     * the AdminHomePage. It sets up the alignment and text fields used  as well as manages the 
     * interactions with the page and handles errors that occur. 
     * 
     * @param primaryStage		Input of primaryStage used to manage the graphics changes
     * @param databaseHelper	Input of the databaseHelper that allows us to interact with the content of the database
     */
    public AdminHomePage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
    	
    	// Initializes the primaryStaged and database helper
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Creates a gridpane and layout for the admin homepage
        adminHomeGrid = new GridPane();
        adminHomeGrid.setAlignment(Pos.CENTER);
        adminHomeGrid.setVgap(10);
        adminHomeGrid.setHgap(10);

        // Initializes buttons and text displayed on the user interface
        Button inviteUserButton = new Button("Invite User");
        Button resetUserAccountButton = new Button("Reset User");
        Button deleteUserAccountButton = new Button("Delete User Account");
        Button listUserAccountsButton = new Button("List User Accounts");
        Button addRemoveRoleButton = new Button("Add/Remove User Role");
        Button articleButton = new Button("Article Edit Functions");
        Button searchArticlesButton = new Button("Search Articles");
        Button articleGroupButton = new Button("Group Functions");
        Button logoutButton = new Button("Log Out");
        Button helpButton = new Button("Help");

        // Adds buttons to gridpane layout on the user interface
        adminHomeGrid.add(inviteUserButton, 0, 0);
        adminHomeGrid.add(deleteUserAccountButton, 2, 0);
        
        adminHomeGrid.add(resetUserAccountButton, 2, 1);
        adminHomeGrid.add(addRemoveRoleButton, 0, 1);
        
        adminHomeGrid.add(listUserAccountsButton, 2, 2);
        adminHomeGrid.add(searchArticlesButton, 0, 2);
        
        adminHomeGrid.add(articleButton, 0, 3);
        adminHomeGrid.add(articleGroupButton, 2, 3);
       
        adminHomeGrid.add(logoutButton, 0, 4);
        adminHomeGrid.add(helpButton, 2, 4);

        // Adds redirect for invite user button when pressed
        inviteUserButton.setOnAction(event -> {
        	// Creates and Redirects to new InviteUserPage and passes Primary Stage and database helper
            InviteUserPage inviteUserPage = new InviteUserPage(primaryStage, databaseHelper, email);
            Scene inviteUserScene = new Scene(inviteUserPage.getInviteUserLayout(), 400, 300);
            primaryStage.setScene(inviteUserScene);
        });

        // Adds redirect for resetUserAccountButton
        resetUserAccountButton.setOnAction(event -> {
        	// Creates and Redirects to new ResetUserAccountPage and passes Primary Stage and database helper
            ResetUserAccountPage resetUserPage = new ResetUserAccountPage(primaryStage, databaseHelper, email);
            Scene resetUserScene = new Scene(resetUserPage.getResetUserLayout(), 400, 300);
            primaryStage.setScene(resetUserScene);
        });

        // Adds redirect for deleteUserAccounButton
        deleteUserAccountButton.setOnAction(event -> {
        	// Creates and Redirects to new DeleteUserAccountPage and passes Primary Stage and database helper
            DeleteUserAccountPage deleteUserPage = new DeleteUserAccountPage(primaryStage, databaseHelper, email);
            Scene deleteUserScene = new Scene(deleteUserPage.getDeleteUserLayout(), 400, 300);
            primaryStage.setScene(deleteUserScene);
        });

        // Adds redirect for listUserAccountsButton
        listUserAccountsButton.setOnAction(event -> {
            try {
                // Gathers the users accounts from the database and attempts to display them
                String usersList = databaseHelper.listUserAccounts();
                showAlert("User Accounts", usersList, Alert.AlertType.INFORMATION); 
                // Checks if there was an error in displaying the user accounts and alerts the user
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while listing user accounts.", Alert.AlertType.ERROR);
            }
        });

        // Adds redirect for addRemoveRoleButton 
        addRemoveRoleButton.setOnAction(event -> {
        	// Creates and redirects to new addRemoveRolePage and passes in primary stage and database helper for usage
            AddRemoveRolePage addRemoveRolePage = new AddRemoveRolePage(primaryStage, databaseHelper, email);
            Scene addRemoveRoleScene = new Scene(addRemoveRolePage.getAddRemoveRoleLayout(), 400, 300);
            primaryStage.setScene(addRemoveRoleScene);
        });
        
        // Adds redirect for addRemoveRoleButton 
        articleButton.setOnAction(event -> {
        	// Creates and redirects to new addRemoveRolePage and passes in primary stage and database helper for usage
        	UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, "Admin");
            Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
            primaryStage.setScene(userHomeScene);
        });

        // Adds redirect for logout button
        logoutButton.setOnAction(event -> {
            // Redirects to newly created LoginPage
            LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
            Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
            primaryStage.setScene(loginScene);
        });
    }
    
    //Get method for the gridlayout of admin homepage
    public GridPane getAdminHomeLayout() {
        return adminHomeGrid;
    }

    // Creates and displays pop up alerts to user 
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
