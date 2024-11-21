package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> UserHomePage. </p>
 * 
 * <p> Description: This class provides the user interface for the home page that users 
 * are redirected to after login. The layout is role-specific, and users can log out 
 * from this page. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 User Home Page
 * @version 2.00	2024-10-30 Project Phase 2 User Home Page
 */

public class InstructorHomePage {

    /** The primary stage used for the Graphical-User-Interface*/
    private final Stage primaryStage;
    
    /** The database helper that allows interactions with the user database */
    private final DatabaseHelper databaseHelper;
    
    /** The email of the logged-in user */
    private final String email;
    
    /** The role of the logged-in user */
    private final String role;
    
    /** The Grid Pane used to structure the user home page UI */
    private final GridPane homeGrid;

    /************
     * This constructor initializes the user home page and sets up all of the 
     * components in the graphical interface, including a welcome message and a 
     * logout button. It also handles the logout action and redirection to the 
     * login page.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
     * @param databaseHelper	The database helper that enables interaction with the database
     * @param email				The email of the logged-in user
     * @param role				The role of the logged-in user
     */
    public InstructorHomePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
    	
    	// Initializes the primaryStage, database helper, email, and role
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup the layout for the user home page using GridPane
        homeGrid = new GridPane();
        homeGrid.setAlignment(Pos.CENTER);
        homeGrid.setVgap(10);
        homeGrid.setHgap(10);
        
        String usedName = "temp!";
        try {
        	databaseHelper.ensureConnection();
        	usedName = databaseHelper.getName(email);
        } catch (Exception e) {
        	e.printStackTrace();
        	showAlert("Error", "An error occurred while accessing users name!", Alert.AlertType.ERROR);
        	
        }

        // Define the welcome label and logout button
        Label welcomeLabel = new Label("Welcome to Instructor Home, " +  usedName + "!");
        
        Button addStudentButton = new Button("Add Student");
        Button deleteStudentButton = new Button("Delete Student");
        
        Button listStudentsButton = new Button("List Students");
        
        Button searchArticlesButton = new Button("Search Articles");
        Button articleFunctionsButton = new Button("Article Functions");
        Button articleGroupsButton = new Button("Article Group Functions");
     
        Button logoutButton = new Button("Log Out");
        Button helpButton = new Button("Help");
        
        

        // Add components to the home grid layout
        homeGrid.add(welcomeLabel, 0, 0);
        
        homeGrid.add(addStudentButton, 0, 1);
        homeGrid.add(deleteStudentButton, 2, 1);
        
        homeGrid.add(listStudentsButton, 0, 2);
        homeGrid.add(searchArticlesButton, 2, 2);
        
        homeGrid.add(articleFunctionsButton, 0, 3); 
        homeGrid.add(articleGroupsButton, 2, 3);
        
        homeGrid.add(logoutButton, 0, 4);
        homeGrid.add(helpButton, 2, 4);

        addStudentButton.setOnAction(event -> {
        	// Redirect to the login page after logout
            AddUserPage addUserPage = new AddUserPage(primaryStage, databaseHelper, email, role);
            Scene addUserScene = new Scene(addUserPage .getAddUserLayout(), 400, 300);
            primaryStage.setScene(addUserScene);
        	
        	System.out.println("Add Student  button pressed");
        });
        
        deleteStudentButton.setOnAction(event -> {
        	// Redirect to the login page after logout
            DeleteUserPage deleteUserPage = new DeleteUserPage(primaryStage, databaseHelper, email, role);
            Scene deleteUserScene = new Scene(deleteUserPage .getDeleteUserLayout(), 400, 300);
            primaryStage.setScene(deleteUserScene);
        	
        	System.out.println("Add Student  button pressed");
        	
        	System.out.println("delete students button pressed");
        	
        });
        
        listStudentsButton.setOnAction(event -> {
        	
        	System.out.println("List students button pressed");
        });
        
        searchArticlesButton.setOnAction(event -> {
        	SearchPage searchPage = new SearchPage(primaryStage, databaseHelper, email, "instructor");
            Scene studentScene = new Scene(searchPage.getSearchLayout(), 400, 300);
            primaryStage.setScene(studentScene);
        });
        
        articleFunctionsButton.setOnAction(event -> {
        	ArticleFunctionsPage articleFunctionsPage = new ArticleFunctionsPage(primaryStage, databaseHelper, email, "instructor");
            Scene articleFunctionsScene = new Scene(articleFunctionsPage.getArticleFunctionsLayout(), 400, 300);
            primaryStage.setScene(articleFunctionsScene);
        });
        
        articleGroupsButton.setOnAction(event -> { 
        	try {
                databaseHelper.ensureConnection();
                String id = databaseHelper.getUserIdFromEmail(email);
                // Creates and redirects to new GroupAccessPage and passes in primary stage and database helper for usage
            	GroupAccessPage groupAccessPage = new GroupAccessPage(primaryStage, databaseHelper, id, email, "instructor");
                Scene groupAccessScene = new Scene(groupAccessPage.getGroupAccessLayout(), 400, 300);
                primaryStage.setScene(groupAccessScene );
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("No user id found with the current email");
            }
        });
     
        
        // Adds functionality for the 'Log Out' button
        logoutButton.setOnAction(event -> {
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
        });
        
        
        helpButton.setOnAction(event -> {
        	// Creates and redirects to new HelpMessagePage and passes in primary stage and database helper for usage
            HelpMessagePage helpMessagePage = new HelpMessagePage(primaryStage, databaseHelper, email, role);
            Scene helpMessageScene = new Scene(helpMessagePage.getHelpMessageLayout(), 400, 300);
            primaryStage.setScene(helpMessageScene);
        });
        
        
        /*
        groupButton.setOnAction(event -> {
        	try {
                databaseHelper.ensureConnection();
                String id = databaseHelper.getUserIdFromEmail(email);
                // Creates and redirects to new GroupAccessPage and passes in primary stage and database helper for usage
            	GroupAccessPage groupAccessPage = new GroupAccessPage(primaryStage, databaseHelper, id, email, role);
                Scene groupAccessScene = new Scene(groupAccessPage.getGroupAccessLayout(), 400, 300);
                primaryStage.setScene(groupAccessScene);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("No user id found with the current email");
            }
        }); **/
                
    	}
    

    // Method to return the user home layout, used in the scene creation
    public GridPane getInstructorHomeLayout() {
        return homeGrid;
    }

    // Helper method to display alerts to the user
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
}
