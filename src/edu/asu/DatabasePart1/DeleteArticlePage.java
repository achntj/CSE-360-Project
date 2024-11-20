package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> DeleteArticlePage. </p>
 * 
 * <p> Description: This class provides a user interface for deleting an article from the database 
 * using the article's unique ID. It includes fields for entering the article ID and options 
 * for deletion or returning to the home page.</p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00	2024-10-30 Project Phase 2 User Home Page
 */

public class DeleteArticlePage {
	
    /** The primary stage used for the Graphical-User-Interface */
	private final Stage primaryStage;
	
    /** The database helper that allows interactions with the article database */
	private final DatabaseHelper databaseHelper;
	
    /** The Grid Pane used to structure the delete article UI */
	private final GridPane deleteArticleGrid;

	/**
     * Constructor that initializes the delete article page and sets up all components in the UI.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
     * @param databaseHelper	The database helper enabling interaction with the database
     * @param email				The email of the logged-in user
     * @param role				The role of the logged-in user
     */
	public DeleteArticlePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
		this.primaryStage = primaryStage;
		this.databaseHelper = databaseHelper;
		
		// Setup layout for the delete article page using GridPane
		deleteArticleGrid = new GridPane();
		deleteArticleGrid.setAlignment(Pos.CENTER);
        deleteArticleGrid.setVgap(10);
        deleteArticleGrid.setHgap(10);
        
        // Define labels and input fields for article deletion
        Label enterIDLabel = new Label("Enter Article ID: ");
        TextField idTextField = new TextField();
        
        Button deleteArticleButton = new Button("Delete Article");
        Button backButton = new Button("Return to UserHome");
        
        // Add components to the delete article grid layout
        deleteArticleGrid.add(enterIDLabel, 0, 0);
        deleteArticleGrid.add(idTextField, 1, 0);
        deleteArticleGrid.add(backButton, 0, 1);
        deleteArticleGrid.add(deleteArticleButton, 1, 1);
        
        // Action event for delete article button
        deleteArticleButton.setOnAction(event -> {
            String idString = idTextField.getText().trim();
            
            // Check if ID is provided and notify user if not
            if (idString.isEmpty()) {
                showAlert("Error", "ID must be provided.", Alert.AlertType.ERROR);
                return;
            }
            
            try {
                int id = Integer.parseInt(idString);
                databaseHelper.deleteArticle(id);
                showAlert("Success", "Article deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid ID.", Alert.AlertType.ERROR);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while deleting the article.", Alert.AlertType.ERROR);
            }
        });
        
        // Action event for back button to return to home page
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

	// Method to return the delete article layout, used in the scene creation
	public GridPane getDeleteArticleScene() {
		return deleteArticleGrid;
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
