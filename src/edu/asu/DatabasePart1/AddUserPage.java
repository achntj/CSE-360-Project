package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> InviteUserPage. </p>
 * 
 * <p> Description: Setup for an interactive JavaFX page that allows an admin user to invite 
 * new users to the system by generating invitation codes tied to specific roles. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 Invite User Page
 * 
 */

public class AddUserPage {

    /** Primary stage used for the GUI Interface */
    private final Stage primaryStage;
    
    /** Allows us to update and edit the database that holds user information and generate invitation codes */
    private final DatabaseHelper databaseHelper;
    
    /** The email of the logged-in user */
    private final String email;
    
    /** The role of the logged-in user */
    private final String role;
    
    /** The Grid Pane used to structure the invite user page */
    private final GridPane inviteUserGrid;

    /************
     * This method initializes all of the elements used in the graphical interface presented for 
     * the InviteUserPage. It sets up the alignment, text fields, and buttons used for the invitation 
     * functionality and provides error handling. 
     * 
     * @param primaryStage		Input of primaryStage used to manage the graphical interface changes
     * @param databaseHelper	Input of the databaseHelper that enables interaction with the database
     */
    public AddUserPage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
    	
    	// Initializes the primaryStage and database helper
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;
        
        // Creates a new GridPane and sets its alignment and spacing
        inviteUserGrid = new GridPane();
        inviteUserGrid.setAlignment(Pos.CENTER);
        inviteUserGrid.setVgap(10);
        inviteUserGrid.setHgap(10);

        // Establishes text fields and buttons used in the user interface
        Label studentLabel = new Label("Student ID");
        TextField studentField = new TextField();
        Label groupRoleLabel = new Label("Role: (admin, instructor, student)");
        TextField groupRoleField = new TextField();
        Label groupLabel = new Label("Group ID");
        TextField groupField = new TextField();
        Button addButton = new Button("Add student");
        Button backButton = new Button("Back");

        // Adds the fields and buttons to the interface
        inviteUserGrid.add(studentLabel, 0, 0);
        inviteUserGrid.add(studentField, 1, 0);
        inviteUserGrid.add(groupRoleLabel, 0, 1);
        inviteUserGrid.add(groupRoleField, 1, 1);
        inviteUserGrid.add(groupLabel, 0, 2);
        inviteUserGrid.add(groupField, 1, 2);
        inviteUserGrid.add(addButton, 0, 3);
        inviteUserGrid.add(backButton, 1, 3);

        // Adds functionality for the 'Generate Invitation Code' button
        addButton.setOnAction(event -> {
            // Retrieves the entered role(s) to associate with the invitation code
            String student = studentField.getText().trim();
            String groupRole = groupRoleField.getText().trim();
            String group = groupField.getText().trim();

            // If no roles are specified, displays an error message
            if (student.isEmpty()) {
                showAlert("Error", "Student must be specified.", Alert.AlertType.ERROR);
                return;
            }
            if (groupRole.isEmpty()) {
                showAlert("Error", "Role(s) must be specified.", Alert.AlertType.ERROR);
                return;
            }
            if (group.isEmpty()) {
                showAlert("Error", "Group must be specified.", Alert.AlertType.ERROR);
                return;
            }

            // Check if the current user is an admin and the group type
            try {
                String groupType = databaseHelper.getGroupType(group); // Retrieves the group type
                String currentUserId = databaseHelper.getUserIdFromEmail(email);
                boolean isAdmin = databaseHelper.isUserAdminInGroup(currentUserId, group); // Checks if the current user is an admin

                if ("special".equalsIgnoreCase(groupType) && !isAdmin) {
                    showAlert("Permission Denied", "You must be an admin to modify a special group.", Alert.AlertType.ERROR);
                    return;
                }

                databaseHelper.updateGroupUsers(group, groupRole, student, true);
                showAlert("Success", "User added!", Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while adding the user.", Alert.AlertType.ERROR);
            }
        });


        // Adds functionality for the 'Back' button to return to the instructor home page
        backButton.setOnAction(event -> {
        	// Navigates back to the Instructor Home Page
            InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper, email, role);
            Scene instructorScene = new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300);
            primaryStage.setScene(instructorScene);
        });
    }

    // Get function for inviteUserGrid layout 
    public GridPane getAddUserLayout() {
        return inviteUserGrid;
    }

    // Creates and displays pop up alert messages to the user 
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
