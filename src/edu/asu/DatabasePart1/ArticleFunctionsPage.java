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

public class ArticleFunctionsPage {

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
    public ArticleFunctionsPage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
    	
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
        Label welcomeLabel = new Label("Welcome to User Home Page, " + role + "!");
        
        Button createArticleButton = new Button("Create Article");
        Button listArticlesButton = new Button("List Articles");
        Button deleteArticleButton = new Button("Delete Article");
        Button backupArticlesButton = new Button("Backup Articles");
        Button restoreArticlesButton = new Button("Restore Articles");
        Button backupByKeywordButton = new Button("Backup Group: ");
        TextField keywordToBackupField = new TextField();
        Button restoreKeywordArticles = new Button("Restore Group:");
        TextField keywordToRestore = new TextField();
        Button logoutButton = new Button("Log Out");
        Button helpButton = new Button("Help:");
        Button groupButton = new Button("View / Edit Groups:");

        // Add components to the home grid layout
        homeGrid.add(welcomeLabel, 0, 0);
        homeGrid.add(createArticleButton, 0, 1);
        homeGrid.add(listArticlesButton, 0, 2);
        homeGrid.add(deleteArticleButton, 0, 3);
        homeGrid.add(backupArticlesButton, 0, 4);
        homeGrid.add(restoreArticlesButton, 0, 5); 
        homeGrid.add(backupByKeywordButton, 0, 6);
        homeGrid.add(keywordToBackupField, 0, 7);
        homeGrid.add(restoreKeywordArticles, 0, 8);
        homeGrid.add(keywordToRestore, 0, 9);
        homeGrid.add(logoutButton, 0, 10);
        homeGrid.add(helpButton, 0, 11);
        homeGrid.add(groupButton, 0, 12);

        createArticleButton.setOnAction(event -> {
        	try {
        		databaseHelper.ensureConnection();
        		
        		//redirect to create article page
        		CreateArticlePage createArticlePage = new CreateArticlePage(primaryStage, databaseHelper, email, role);
                Scene createArticleScene = new Scene(createArticlePage.getCreateArticleLayout(), 400, 300);
                primaryStage.setScene(createArticleScene);
        	} catch (Exception e) {
        		e.printStackTrace();
                showAlert("Error", "An error occurred while entering create and article.", Alert.AlertType.ERROR);
        	}
        });
        
        listArticlesButton.setOnAction(event -> {
        	try {
                // Gathers the users accounts from the database and attempts to display them
                String articleList = databaseHelper.listArticles();
                showAlert("Articles", articleList, Alert.AlertType.INFORMATION); 
                // Checks if there was an error in displaying the user accounts and alerts the user
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while listing articles.", Alert.AlertType.ERROR);
            }
        	
        });
        
        deleteArticleButton.setOnAction(event -> {
        	
        	try {
        		databaseHelper.ensureConnection();
        		
        		//redirect to create article page
        		DeleteArticlePage deleteArticlePage = new DeleteArticlePage(primaryStage, databaseHelper, email, role);
                Scene deleteArticleScene = new Scene(deleteArticlePage.getDeleteArticleScene(), 400, 300);
                primaryStage.setScene(deleteArticleScene);
        	} catch (Exception e) {
        		e.printStackTrace();
                showAlert("Error", "An error occurred while entering create and article.", Alert.AlertType.ERROR);
        	}
        });
        
        backupArticlesButton.setOnAction(event -> {
            showAlert("Info", "Backing up Articles...", Alert.AlertType.INFORMATION);
            try {
                databaseHelper.ensureConnection();
                databaseHelper.backupArticles("articleBackup.txt");
                showAlert("Success", "Backup created successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while backing up articles.", Alert.AlertType.ERROR);
            }
        });
        
        backupByKeywordButton.setOnAction(event -> {
        	String keyword = keywordToBackupField.getText().trim();
        	
        	if (keyword.isEmpty()){
        		showAlert("Error", "Keyword is empty, please add a keyword!", Alert.AlertType.ERROR);
        		return;
        	}

        	try {
        		String filename = "backup" + keyword + ".txt";
        		
        		databaseHelper.ensureConnection();
        		databaseHelper.backupByKeyword(filename, keyword);
        		showAlert("Success", "Backup created successfully.", Alert.AlertType.INFORMATION);
        		
        	} catch (Exception e){
        		e.printStackTrace();
                showAlert("Error", "An error occurred while backing up articles.", Alert.AlertType.ERROR);
        	}
        });

        restoreArticlesButton.setOnAction(event -> {
            showAlert("Info", "Restoring Articles...", Alert.AlertType.INFORMATION);
            try {
                databaseHelper.ensureConnection();
                databaseHelper.restoreArticles("articleBackup.txt");
                showAlert("Success", "Restore completed successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while restoring articles.", Alert.AlertType.ERROR);
            }
        });
        
        restoreKeywordArticles.setOnAction(event -> {
        	String keyword = keywordToRestore.getText().trim();
        	String filename = "backup" + keyword + ".txt";
        	
        	if (keyword.isEmpty()){
        		showAlert("Error", "Keyword is empty, please add a keyword!", Alert.AlertType.ERROR);
        		return;
        	}
        	
            showAlert("Info", "Restoring Articles...", Alert.AlertType.INFORMATION);
            try {
                databaseHelper.ensureConnection();
                databaseHelper.restoreArticlesByKeyword(filename, keyword);
                showAlert("Success", "Restore completed successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while restoring articles.", Alert.AlertType.ERROR);
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
            primaryStage.setScene(helpMessageScene );
        });
        groupButton.setOnAction(event -> {
        	try {
                databaseHelper.ensureConnection();
                String id = databaseHelper.getUserIdFromEmail(email);
                // Creates and redirects to new GroupAccessPage and passes in primary stage and database helper for usage
            	GroupAccessPage groupAccessPage = new GroupAccessPage(primaryStage, databaseHelper, id, email, role);
                Scene groupAccessScene = new Scene(groupAccessPage.getGroupAccessLayout(), 400, 300);
                primaryStage.setScene(groupAccessScene );
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("No user id found with the current email");
            }
        });
                
    }

    // Method to return the user home layout, used in the scene creation
    public GridPane getArticleFunctionsLayout() {
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