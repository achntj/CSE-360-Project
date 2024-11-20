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
        Button helpButton = new Button("Help");


        // Add components to the home grid layout
        homeGrid.add(searchButton, 0, 0);
        homeGrid.add(searchCriteria, 2, 0);
 
        try {
        	
        	homeGrid.add(filterDifficultyLabel, 0, 1);
        	homeGrid.add(filterGroupsLabel, 2, 1);
        	
        	RadioButton allDifficulties = new RadioButton("All Difficulty Levels");
        	RadioButton beginnerLevels = new RadioButton("Beginner");
        	RadioButton intermediateLevels = new RadioButton("Intermediate");
        	RadioButton advancedLevels = new RadioButton("Advanced");
        	
        	allDifficulties.setToggleGroup(difficultyToggle);
        	beginnerLevels.setToggleGroup(difficultyToggle);
        	intermediateLevels.setToggleGroup(difficultyToggle);
        	advancedLevels.setToggleGroup(difficultyToggle);
        	
            homeGrid.add(allDifficulties, 0, 2);
            homeGrid.add(beginnerLevels, 0, 3);
            homeGrid.add(intermediateLevels, 0, 4);
            homeGrid.add(advancedLevels, 0, 5);
          
            String[] groups = {"group1", "group2"};//databaseHelper.getAccessibleGroups(email);
            int row = 2;

            // Add radio buttons for each role in the grid
            for (String group : groups) {
                RadioButton groupOption = new RadioButton(group);
                groupOption.setToggleGroup(groupsToggle);
                homeGrid.add(groupOption, 2, row++);
            }

            int lastLayer = 6;
            if (lastLayer < row) {
            	lastLayer = row;
            }
            homeGrid.add(logoutButton, 0, lastLayer);
            homeGrid.add(helpButton, 2, lastLayer);
            
            searchButton.setOnAction(event -> {
            	
            	RadioButton selectedDifficulty = (RadioButton) difficultyToggle.getSelectedToggle();
                if (selectedDifficulty == null) {
                    //Assume default all
                }
                String difficultyFilter = selectedDifficulty.getText().trim();
                System.out.println("DIFFICULTY FILTER CHOSEN: " + difficultyFilter);
                
                RadioButton selectedGroup = (RadioButton) groupsToggle.getSelectedToggle();
                if (selectedGroup == null) {
                    //assume default all
                }
            	String groupFilter = selectedGroup.getText().trim();
            	System.out.println("GROUP FILTER CHOSEN: " + groupFilter);
            	
            	
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while retrieving accessible groups.", Alert.AlertType.ERROR);
        }
        
                
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
