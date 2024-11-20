package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> CreateArticlePage. </p>
 * 
 * <p> Description: This class represents the UI for creating a new article entry. 
 * It provides fields for the article's title, difficulty level, authors, abstract, 
 * keywords, body, and references. Users can also return to the homepage from this page.</p>
 * 
* <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00	2024-10-30 Project Phase 2 User Home Page
 */

public class CreateGeneralGroup {
	
    /** The primary stage used for the Graphical-User-Interface */
	private final Stage primaryStage;
	
    /** The database helper that allows interactions with the article database */
	private final DatabaseHelper databaseHelper;
	
    /** The email of the logged-in user */
	private final String email;
	
	/** The role of the logged-in user */
    private final String role;
	
    /** The Grid Pane used to structure the create article UI */
	private final GridPane createGroupGrid;

	/**
     * Constructor that initializes the create article page and sets up all components in the UI.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
     * @param databaseHelper	The database helper enabling interaction with the database
     * @param email				The email of the logged-in user
     * @param role				The role of the logged-in user
     */
	public CreateGeneralGroup(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
		this.primaryStage = primaryStage;
		this.databaseHelper = databaseHelper;
		this.email = email;
		this.role = role;
		
		// Setup layout for the create group page using GridPane
        createGroupGrid = new GridPane();
        createGroupGrid.setAlignment(Pos.CENTER);
        createGroupGrid.setVgap(10);
        createGroupGrid.setHgap(10);

        // Define labels and input fields for group details
        Label groupNameLabel = new Label("Group Name: ");
        TextField groupNameTextField = new TextField();

        Label articlesLabel = new Label("Articles (comma-separated ids):");
        TextField articlesTextField = new TextField();

        Label adminsLabel = new Label("Admins (comma-separated ids):");
        TextField adminsTextField = new TextField();

        Label instructorsLabel = new Label("Instructors (comma-separated ids):");
        TextField instructorsTextField = new TextField();

        Label studentsLabel = new Label("Students (comma-separated ids):");
        TextField studentsTextField = new TextField();

        Button createGroupButton = new Button("Create Group");
        Button backButton = new Button("Return to Homepage");

        // Add components to the create group grid layout
        createGroupGrid.add(groupNameLabel, 0, 0);
        createGroupGrid.add(groupNameTextField, 1, 0);

        createGroupGrid.add(articlesLabel, 0, 1);
        createGroupGrid.add(articlesTextField, 1, 1);

        createGroupGrid.add(adminsLabel, 0, 2);
        createGroupGrid.add(adminsTextField, 1, 2);

        createGroupGrid.add(instructorsLabel, 0, 3);
        createGroupGrid.add(instructorsTextField, 1, 3);

        createGroupGrid.add(studentsLabel, 0, 4);
        createGroupGrid.add(studentsTextField, 1, 4);

        createGroupGrid.add(backButton, 0, 5);
        createGroupGrid.add(createGroupButton, 1, 5);

        // Action event for create group button
        createGroupButton.setOnAction(event -> {
            String groupName = groupNameTextField.getText().trim();
            String articles = articlesTextField.getText().trim();
            String admins = adminsTextField.getText().trim();
            String instructors = instructorsTextField.getText().trim();
            String students = studentsTextField.getText().trim();

            // Check for empty fields and notify user if found
            if (groupName.isEmpty() || admins.isEmpty()) {
                showAlert("Error", "Group name and admins are required fields!", Alert.AlertType.ERROR);
                return;
            }

            // Attempt to create the group and handle exceptions if they occur
            try {
                databaseHelper.createGroup(groupName, articles, admins, instructors, students);
                showAlert("Success", "Group Created Successfully!", Alert.AlertType.INFORMATION);

                // Redirect to user home page after group creation
                UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, role);
                Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
                primaryStage.setScene(userHomeScene);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while creating the group.", Alert.AlertType.ERROR);
            }
        });

        // Action event for back button to return to home page
        backButton.setOnAction(event -> {
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
	
	// Method to return the create article layout, used in the scene creation
	public GridPane getCreateGroupLayout() {
        return createGroupGrid;
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
