package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> GroupAccessPage. </p>
 * 
 * <p> Description: This class provides an interactive JavaFX interface that allows 
 * users to manage groups within the system. Users can create, view, delete, and 
 * back up groups, among other functionalities. The layout and features adapt to the 
 * user's role. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-11-18 Project Phase 3 Group Management Page
 */
public class EditGroupsPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** Handles database operations for group management. */
    private final DatabaseHelper databaseHelper;

 
    /** The email of the logged-in user. */
    private final String email;

    /** The role of the logged-in user. */
    private final String role;

    /** The GridPane used to structure the group access page UI. */
    private final GridPane groupGrid;

    /**
     * Constructs the GroupAccessPage with the given parameters.
     * Initializes the graphical interface for group management and handles 
     * interactions for creating, viewing, deleting, and backing up groups.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param user_id        the ID of the logged-in user
     * @param email          the email of the logged-in user
     * @param role           the role of the logged-in user
     */
    public EditGroupsPage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup the layout for the group access page using GridPane
        groupGrid = new GridPane();
        groupGrid.setAlignment(Pos.CENTER);
        groupGrid.setVgap(10);
        groupGrid.setHgap(10);

        // Define UI components
        Button addStudentButton = new Button("Add Student");
        Button removeStudentButton = new Button("Remove Student");
       
        Label articleIDLabel = new Label("Article ID: ");
        TextField articleField = new TextField();
        Label groupIDLabel = new Label("Group ID: ");
        TextField groupField = new TextField();
        
        Button addArticlesButton = new Button("Add To Group");
        Button deleteArticlesButton = new Button("Delete From Group");
        
        
        Button helpButton = new Button("Help");
        Button backButton = new Button("Back");

        // Add components to the GridPane layout
        groupGrid.add(addStudentButton, 0, 0);
        groupGrid.add(removeStudentButton, 2, 0);
        
        groupGrid.add(articleIDLabel, 0, 1);
        groupGrid.add(articleField, 2, 1);
        groupGrid.add(groupIDLabel, 0, 2);
        groupGrid.add(groupField, 2, 2);
       
        groupGrid.add(addArticlesButton, 0, 3);
        groupGrid.add(deleteArticlesButton, 2, 3);
       
        groupGrid.add(helpButton, 2, 4);
        groupGrid.add(backButton, 0, 4);

        // Setup button actions
        addStudentButton.setOnAction(event -> navigateToAddUser());
        removeStudentButton.setOnAction(event -> navigateToDeleteUser());
        addArticlesButton.setOnAction(event -> addArticleToGroup(articleField, groupField));
        deleteArticlesButton.setOnAction(event -> removeArticleFromGroup(articleField, groupField));
        helpButton.setOnAction(event -> navigateToHelpPage());
        backButton.setOnAction(event -> navigateBack());
    }

    /**
     * Redirects to the Create General Group page.
     */
    private void navigateToAddUser() {
    	AddUserPage addUserPage = new AddUserPage(primaryStage, databaseHelper, email, role);
        Scene addUserScene = new Scene(addUserPage.getAddUserLayout(), 400, 300);
        primaryStage.setScene(addUserScene);
    }

    /**
     * Redirects to the Create Special Group page.
     */
    private void navigateToDeleteUser() {
    	DeleteUserPage deleteUserPage = new DeleteUserPage(primaryStage, databaseHelper, email, role);
        Scene deleteScene = new Scene(deleteUserPage.getDeleteUserLayout(), 400, 300);
        primaryStage.setScene(deleteScene);
    }

    /**
     * Displays a list of all groups available in the database.
     */
    private void addArticleToGroup(TextField articleField, TextField groupField) {
        String articleID = articleField.getText().trim();
        String groupID = groupField.getText().trim();
        
        if (articleID.isBlank() || groupID.isBlank()) {
        	showAlert("Error", "Please provide an group and article!", Alert.AlertType.ERROR);
        	return;
        }
        
        try {
        	databaseHelper.ensureConnection();
        	databaseHelper.updateGroupArticles(groupID, articleID, true);
        } catch (Exception e) {
			e.printStackTrace();
			System.out.println("Issues Adding Article to Group");
		}
        
    }
    
    /**
     * 
     */
    private void removeArticleFromGroup(TextField articleField, TextField groupField) {
    	 String articleID = articleField.getText().trim();
         String groupID = groupField.getText().trim();
         
         if (articleID.isBlank() || groupID.isBlank()) {
         	showAlert("Error", "Please provide an group and article!", Alert.AlertType.ERROR);
         	return;
         }
         
         try {
         	databaseHelper.ensureConnection();
         	databaseHelper.updateGroupArticles(groupID, articleID, false);
         } catch (Exception e) {
 			e.printStackTrace();
 			System.out.println("Issues Adding Article to Group");
 		}
    }

    /**
     * Navigates to the Help page.
     */
    private void navigateToHelpPage() {
        HelpMessagePage helpMessagePage = new HelpMessagePage(primaryStage, databaseHelper, email, role);
        Scene helpScene = new Scene(helpMessagePage.getHelpMessageLayout(), 400, 300);
        primaryStage.setScene(helpScene);
    }

    /**
     * Navigates back to the user's respective home page based on their role.
     */
    private void navigateBack() {
    	try {
			databaseHelper.ensureConnection();
			String id = databaseHelper.getUserIdFromEmail(email);
			// Creates and redirects to new GroupAccessPage and passes in primary stage and
			// database helper for usage
			GroupAccessPage groupAccessPage = new GroupAccessPage(primaryStage, databaseHelper, id, email, role);
			Scene groupAccessScene = new Scene(groupAccessPage.getGroupAccessLayout(), 400, 300);
			primaryStage.setScene(groupAccessScene);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No user id found with the current email");
		}
    }

    /**
     * Returns the group access layout, used in scene creation.
     * 
     * @return the GridPane layout of the Group Access Page
     */
    public GridPane getGroupAccessLayout() {
        return groupGrid;
    }

    /**
     * Displays an alert with the specified title, content, and alert type.
     * 
     * @param title     the title of the alert
     * @param content   the content of the alert
     * @param alertType the type of alert (e.g., ERROR, INFORMATION)
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}