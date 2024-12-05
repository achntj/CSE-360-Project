package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> DeleteUserPage. </p>
 * 
 * <p> Description: This class provides an interactive JavaFX interface that allows 
 * instructors or admins to remove a user from a specific group or role. The functionality 
 * includes validation checks and ensures proper permissions are enforced before modifying 
 * group memberships. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Project Phase 1 Delete User Page
 */
public class DeleteUserPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** Handles database operations for user and group management. */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in user. */
    private final String email;

    /** The role of the logged-in user. */
    private final String role;

    /** The GridPane used to structure the delete user page UI. */
    private final GridPane inviteUserGrid;

    /**
     * Constructs the DeleteUserPage with the given parameters.
     * Initializes the graphical interface for deleting a user from a group or role
     * and handles interactions, including validations and error handling.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in user
     * @param role           the role of the logged-in user
     */
    public DeleteUserPage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup the layout for the delete user page using GridPane
        inviteUserGrid = new GridPane();
        inviteUserGrid.setAlignment(Pos.CENTER);
        inviteUserGrid.setVgap(10);
        inviteUserGrid.setHgap(10);

        // Define UI components
        Label studentLabel = new Label("Student ID:");
        TextField studentField = new TextField();
        Label groupRoleLabel = new Label("Role: (admins, instructors, students)");
        TextField groupRoleField = new TextField();
        Label groupLabel = new Label("Group ID:");
        TextField groupField = new TextField();
        Button deleteButton = new Button("Delete Student");
        Button backButton = new Button("Back");

        // Add components to the GridPane layout
        inviteUserGrid.add(studentLabel, 0, 0);
        inviteUserGrid.add(studentField, 1, 0);
        inviteUserGrid.add(groupRoleLabel, 0, 1);
        inviteUserGrid.add(groupRoleField, 1, 1);
        inviteUserGrid.add(groupLabel, 0, 2);
        inviteUserGrid.add(groupField, 1, 2);
        inviteUserGrid.add(deleteButton, 0, 3);
        inviteUserGrid.add(backButton, 1, 3);

        // Setup button actions
        deleteButton.setOnAction(event -> deleteUser(studentField.getText().trim(), groupRoleField.getText().trim(), groupField.getText().trim()));
        backButton.setOnAction(event -> navigateBackToHomePage());
    }

    /**
     * Handles the deletion of a user from a group or role.
     * Validates inputs, checks permissions, and updates the database.
     * 
     * @param student   the ID of the student to delete
     * @param groupRole the role (admins, instructors, or students) to delete the user from
     * @param group     the ID of the group from which the user should be removed
     */
    private void deleteUser(String student, String groupRole, String group) {
        // Validate inputs
        if (student.isEmpty()) {
            showAlert("Error", "Student ID must be specified.", Alert.AlertType.ERROR);
            return;
        }
        if (groupRole.isEmpty()) {
            showAlert("Error", "Role must be specified.", Alert.AlertType.ERROR);
            return;
        }
        if (group.isEmpty()) {
            showAlert("Error", "Group ID must be specified.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Ensure the user has permission to modify the group
            String groupType = databaseHelper.getGroupType(group);
            String currentUserId = databaseHelper.getUserIdFromEmail(email);
            boolean isAdmin = databaseHelper.isUserAdminInGroup(currentUserId, group);

            if ("special".equalsIgnoreCase(groupType) && !isAdmin) {
                showAlert("Permission Denied", "You must be an admin to modify a special group.", Alert.AlertType.ERROR);
                return;
            }

            // Proceed with deleting the user
            databaseHelper.updateGroupUsers(group, groupRole, student, false);
            showAlert("Success", "User deleted successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while deleting the user.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Navigates back to the home page of the logged-in user.
     * Redirects to the appropriate home page based on the user's role.
     */
    private void navigateBackToHomePage() {
    	EditGroupsPage editGroupsPage = new EditGroupsPage(primaryStage, databaseHelper, email, role);
		Scene editGroupsScene = new Scene(editGroupsPage.getGroupAccessLayout(), 400, 300);
		primaryStage.setScene(editGroupsScene);
    }

    /**
     * Returns the layout of the delete user page, used in scene creation.
     * 
     * @return the GridPane layout of the DeleteUserPage
     */
    public GridPane getDeleteUserLayout() {
        return inviteUserGrid;
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