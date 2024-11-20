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

public class SearchDisplayPage {

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
    public SearchDisplayPage(Stage primaryStage, DatabaseHelper databaseHelper,  String email, String role, String searchContent, int articleNumbers, String group) {
    	
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
        
        Label activeGroups = new Label("Currently Active Group: " + group);
        Label numberOfArticles = new Label("Number of Articles: " + articleNumbers);
        
        Label listedArticles = new Label(searchContent);
        listedArticles.setWrapText(true);
        
        
        Button backToSearchButton = new Button("Search");
        Button displayArticleButton = new Button("View Article (ENTER ID): ");
        TextField articleID = new TextField();


        homeGrid.add(activeGroups, 0, 0);
        homeGrid.add(numberOfArticles, 1, 0);
       
        homeGrid.add(listedArticles, 0, 2);
        
        homeGrid.add(backToSearchButton, 0, 10);
        homeGrid.add(displayArticleButton, 0, 11);
        homeGrid.add(articleID, 0, 12);
        
        backToSearchButton.setOnAction(event-> {
        	SearchPage searchPage = new SearchPage(primaryStage, databaseHelper, email, "admin");
            Scene searchScene = new Scene(searchPage.getSearchLayout(), 400, 300);
            primaryStage.setScene(searchScene);
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
