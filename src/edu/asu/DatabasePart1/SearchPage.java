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
    
    private String[] groups;

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
        this.groups = null;

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

        RadioButton beginnerLevels = new RadioButton("Beginner");
        RadioButton intermediateLevels = new RadioButton("Intermediate");
        RadioButton advancedLevels = new RadioButton("Advanced");
        RadioButton expertLevels = new RadioButton("Expert");


        // Add components to the home grid layout
        homeGrid.add(searchButton, 0, 0);
        homeGrid.add(searchCriteria, 2, 0);
 
        homeGrid.add(filterDifficultyLabel, 0, 1);
        homeGrid.add(filterGroupsLabel, 2, 1);

        beginnerLevels.setToggleGroup(difficultyToggle);
        intermediateLevels.setToggleGroup(difficultyToggle);
        advancedLevels.setToggleGroup(difficultyToggle);
        expertLevels.setToggleGroup(difficultyToggle);
        
        homeGrid.add(beginnerLevels, 0, 2);
        homeGrid.add(intermediateLevels, 0, 3);
        homeGrid.add(advancedLevels, 0, 4);
        homeGrid.add(expertLevels, 0, 5);
   
        int lastLayer = 6;
        int row = 2;
    
   
        try{
            groups = databaseHelper.groupsAccessibleToUser(email);
        }
        catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while retrieving accessible groups.", Alert.AlertType.ERROR);
        }

        if (groups == null) {
            System.out.println("No Groups Accessible!");
            
            Label noGroupsLabel = new Label("No Groups Avalible!");
            homeGrid.add(noGroupsLabel, 2, row);
        }
        else {
            for (String group : groups) {
                RadioButton groupOption = new RadioButton(group);
                groupOption.setToggleGroup(groupsToggle);
                homeGrid.add(groupOption, 2, row++);
            }    
            if (lastLayer < row) {
                lastLayer = row;
            }
        }
        
        homeGrid.add(logoutButton, 1, lastLayer);
        homeGrid.add(backButton, 0, lastLayer);
        homeGrid.add(helpButton, 2, lastLayer);
    

        
        searchButton.setOnAction(event -> {
            	
            int[] filteredArticleList = null;

            RadioButton selectedGroup = (RadioButton) groupsToggle.getSelectedToggle();
            String groupFilter = null;
            RadioButton selectedDifficulty = (RadioButton) difficultyToggle.getSelectedToggle();
            String difficultyFilter = null;
            
            String search = searchCriteria.getText().trim();
            
            if (groups != null){
                try{
                    if (selectedGroup == null) {
                        groupFilter = "ALL";
                        filteredArticleList = databaseHelper.getArticlesInGroupList(groups, "*");
                    }
                    else {
                        groupFilter = selectedGroup.getText().trim();
                        filteredArticleList = databaseHelper.getArticlesInGroupList(groups, groupFilter);
                    }
    
                    if (selectedDifficulty == null) {
                        difficultyFilter = "ALL";
                        filteredArticleList = databaseHelper.articlesFilteredDifficulty(filteredArticleList, "*");
                    }
                    else {
                        difficultyFilter = selectedDifficulty.getText().trim();
                        filteredArticleList = databaseHelper.articlesFilteredDifficulty(filteredArticleList, difficultyFilter);
                    }
                    
                    if (search != null) {
                    	 filteredArticleList = databaseHelper.searchForArticles(search, filteredArticleList);
                    }
                    
                } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Error", "An error occurred while filtering the search!.", Alert.AlertType.ERROR);
                }
            }
           
            int numberOfArticlesUnderSearch = 0;
            String listOfArticles = null;
            if (filteredArticleList == null) {
                listOfArticles = "No Articles Found!";
            }
            else {
                try{
                    listOfArticles = databaseHelper.getArticlesFromList(filteredArticleList);
                    numberOfArticlesUnderSearch = filteredArticleList.length;
                } catch(Exception e){
                    e.printStackTrace();
                    showAlert("Error", "An error occurred retriving articles.", Alert.AlertType.ERROR);
                }
            }
           
            	SearchDisplayPage searchDisplayPage = new SearchDisplayPage(primaryStage, databaseHelper, email, role, listOfArticles, numberOfArticlesUnderSearch, groupFilter, filteredArticleList);
            	Scene searchDisplayScene = new Scene(searchDisplayPage.getSearchLayout(), 400, 300);
                primaryStage.setScene(searchDisplayScene);      			
            
        });
        
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
