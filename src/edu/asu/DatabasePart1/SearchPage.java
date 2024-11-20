package edu.asu.DatabasePart1;

import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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

public class SearchPage {

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
    public SearchPage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
    	
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

        // Define the welcome label and logout button
        Button searchButton = new Button("Search: ");
        TextField searchCriteria = new TextField();
        
        Label filterDifficultyLabel = new Label("Filter Article Difficulty: ");
        ToggleGroup difficultyToggle = new ToggleGroup();
        
        Label filterGroupsLabel = new Label("Filter Groups Displayed: ");
        ToggleGroup groupsToggle = new ToggleGroup();
        
        Button logoutButton = new Button("Log Out");
        Button backButton = new Button("Back");
        Button helpButton = new Button("Help");


        // Add components to the home grid layout
        homeGrid.add(searchButton, 0, 0);
        homeGrid.add(searchCriteria, 2, 0);
 
        try {
        	
        	homeGrid.add(filterDifficultyLabel, 0, 1);
        	homeGrid.add(filterGroupsLabel, 2, 1);
        	
            RadioButton beginnerLevels = new RadioButton("Beginner");
        	RadioButton intermediateLevels = new RadioButton("Intermediate");
        	RadioButton advancedLevels = new RadioButton("Advanced");

        	beginnerLevels.setToggleGroup(difficultyToggle);
        	intermediateLevels.setToggleGroup(difficultyToggle);
        	advancedLevels.setToggleGroup(difficultyToggle);
        	
            homeGrid.add(beginnerLevels, 0, 2);
            homeGrid.add(intermediateLevels, 0, 3);
            homeGrid.add(advancedLevels, 0, 4);
          
            String[] groups = databaseHelper.groupsAccessibleToUser(email);
            int row = 2;

            // Add radio buttons for each role in the grid
            for (String group : groups) {
                RadioButton groupOption = new RadioButton(group);
                groupOption.setToggleGroup(groupsToggle);
                homeGrid.add(groupOption, 2, row++);
            }

            int lastLayer = 5;
            if (lastLayer < row) {
            	lastLayer = row;
            }
            homeGrid.add(logoutButton, 1, lastLayer);
            homeGrid.add(backButton, 0, lastLayer);
            homeGrid.add(helpButton, 2, lastLayer);
            
            searchButton.setOnAction(event -> {
            	
            	RadioButton selectedDifficulty = (RadioButton) difficultyToggle.getSelectedToggle();
            	String difficultyFilter;
            	
                if (selectedDifficulty == null) {
                	difficultyFilter = "ALL";
                }
                else {
                	difficultyFilter = selectedDifficulty.getText().trim();
                }
         
                
                RadioButton selectedGroup = (RadioButton) groupsToggle.getSelectedToggle();
                String groupFilter;
                if (selectedGroup == null) {
                	groupFilter = "ALL";
                }
                else {
                	groupFilter = selectedGroup.getText().trim();
                }
                int[] filteredGroupList = null;
            	
                try {
	                if (groupFilter != "ALL") {
	                	filteredGroupList = databaseHelper.getArticlesInGroupList(groups, groupFilter);
	                }
	                else {
	                	filteredGroupList = databaseHelper.getArticlesInGroupList(groups, "*");
	                }
	                if (difficultyFilter != "ALL") {
	                	filteredGroupList = databaseHelper.articlesFilteredDifficulty(filteredGroupList, difficultyFilter);
	                }
	                else {
	                	filteredGroupList = databaseHelper.articlesFilteredDifficulty(filteredGroupList, "*");
	                }
                } catch (Exception e) {
                	e.printStackTrace();
                    showAlert("Error", "An error occurred during filtering.", Alert.AlertType.ERROR);
                }
            	
            	String listOfArticles;
                if (filteredGroupList == null) {
                	listOfArticles = "No Articles Found!";
                }
                else {
                	listOfArticles = databaseHelper.getArticlesFromList(filteredGroupList);
                }
                
            	SearchDisplayPage searchDisplayPage = new SearchDisplayPage(primaryStage, databaseHelper, email, role, listOfArticles, 2, groupFilter);
            	Scene searchDisplayScene = new Scene(searchDisplayPage.getSearchLayout(), 400, 300);
                primaryStage.setScene(searchDisplayScene);      			
            
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while retrieving accessible groups.", Alert.AlertType.ERROR);
       
        
        }
        
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
        
        backButton.setOnAction(event -> {
        	
        	if (role.equalsIgnoreCase("admin")) {                   
                AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
                Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
                primaryStage.setScene(adminScene);
            }
            
            else if (role.equalsIgnoreCase("instructor")) {                   
                InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper, email, "instructor");
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

    // Method to return the user home layout, used in the scene creation
    public GridPane getSearchLayout() {
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
