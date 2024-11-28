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
public class GroupAccessPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** Handles database operations for group management. */
    private final DatabaseHelper databaseHelper;

    /** The ID of the logged-in user. */
    private final String user_id;

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
    public GroupAccessPage(Stage primaryStage, DatabaseHelper databaseHelper, String user_id, String email, String role) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.user_id = user_id;
        this.email = email;
        this.role = role;

        // Setup the layout for the group access page using GridPane
        groupGrid = new GridPane();
        groupGrid.setAlignment(Pos.CENTER);
        groupGrid.setVgap(10);
        groupGrid.setHgap(10);

        // Define UI components
        Button createGroupButton = new Button("Create General Group");
        Button createSpecialButton = new Button("Create Special Access Group");
        Button listGroupsButton = new Button("List Groups");
        Label groupIDLabel = new Label("Group ID: ");
        TextField groupField = new TextField();
        Button viewGroupButton = new Button("View Group");
        Button deleteGroupButton = new Button("Delete Group");
        Button backupGroupButton = new Button("Backup Group");
        Button restoreGroupButton = new Button("Restore Group");
        Button helpButton = new Button("Help");
        Button backButton = new Button("Back");

        // Add components to the GridPane layout
        groupGrid.add(createGroupButton, 0, 0);
        groupGrid.add(createSpecialButton, 1, 0);
        groupGrid.add(listGroupsButton, 2, 0);
        groupGrid.add(groupIDLabel, 0, 1);
        groupGrid.add(groupField, 2, 1);
        groupGrid.add(viewGroupButton, 0, 2);
        groupGrid.add(deleteGroupButton, 2, 2);
        groupGrid.add(backupGroupButton, 0, 3);
        groupGrid.add(restoreGroupButton, 2, 3);
        groupGrid.add(helpButton, 2, 4);
        groupGrid.add(backButton, 0, 4);

        // Setup button actions
        createGroupButton.setOnAction(event -> navigateToCreateGeneralGroup());
        createSpecialButton.setOnAction(event -> navigateToCreateSpecialGroup());
        listGroupsButton.setOnAction(event -> displayGroupList());
        viewGroupButton.setOnAction(event -> viewGroup(groupField.getText().trim()));
        deleteGroupButton.setOnAction(event -> deleteGroup(groupField.getText().trim()));
        backupGroupButton.setOnAction(event -> backupGroups());
        restoreGroupButton.setOnAction(event -> restoreGroups());
        helpButton.setOnAction(event -> navigateToHelpPage());
        backButton.setOnAction(event -> navigateBack());
    }

    /**
     * Redirects to the Create General Group page.
     */
    private void navigateToCreateGeneralGroup() {
        try {
            databaseHelper.ensureConnection();
            CreateGeneralGroupPage createGeneralGroupPage = new CreateGeneralGroupPage(primaryStage, databaseHelper, email, role);
            Scene createGeneralScene = new Scene(createGeneralGroupPage.getCreateGroupLayout(), 400, 300);
            primaryStage.setScene(createGeneralScene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while entering the Create Group page.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Redirects to the Create Special Group page.
     */
    private void navigateToCreateSpecialGroup() {
        try {
            databaseHelper.ensureConnection();
            CreateSpecialGroupPage createSpecialGroupPage = new CreateSpecialGroupPage(primaryStage, databaseHelper, email, role);
            Scene createSpecialScene = new Scene(createSpecialGroupPage.getCreateGroupLayout(), 400, 300);
            primaryStage.setScene(createSpecialScene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while entering the Create Group page.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays a list of all groups available in the database.
     */
    private void displayGroupList() {
        try {
            String groupList = databaseHelper.listGroups();
            showAlert("Groups", groupList, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while listing groups.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays details about a specific group.
     * 
     * @param groupId the ID of the group to view
     */
    private void viewGroup(String groupId) {
        if (groupId.isEmpty()) {
            showAlert("Error", "Group ID must be specified.", Alert.AlertType.ERROR);
            return;
        }
        try {
            String groupInfo = databaseHelper.getGroupInfo(groupId);
            showAlert("Group Info", groupInfo, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while retrieving group information.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Deletes a group by its ID.
     * 
     * @param groupId the ID of the group to delete
     */
    private void deleteGroup(String groupId) {
        if (groupId.isEmpty()) {
            showAlert("Error", "Group ID must be specified.", Alert.AlertType.ERROR);
            return;
        }
        try {
            databaseHelper.ensureConnection();
            databaseHelper.deleteGroup(groupId);
            showAlert("Success", "Group deleted successfully.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while deleting the group.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Backs up all group information to a file.
     */
    private void backupGroups() {
        showAlert("Info", "Backing up groups...", Alert.AlertType.INFORMATION);
        try {
            databaseHelper.ensureConnection();
            databaseHelper.backupGroups("groupBackup.txt");
            showAlert("Success", "Backup created successfully.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while backing up groups.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Restores groups from a backup file.
     */
    private void restoreGroups() {
        showAlert("Info", "Restoring groups...", Alert.AlertType.INFORMATION);
        try {
            databaseHelper.ensureConnection();
            databaseHelper.restoreGroups("groupBackup.txt");
            showAlert("Success", "Restore completed successfully.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while restoring groups.", Alert.AlertType.ERROR);
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
            if (role.equalsIgnoreCase("admin")) {
                AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
                primaryStage.setScene(new Scene(adminHomePage.getAdminHomeLayout(), 400, 300));
            } else if (role.equalsIgnoreCase("instructor")) {
                InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper, email, role);
                primaryStage.setScene(new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300));
            } else {
                databaseHelper.ensureConnection();
                showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                primaryStage.setScene(new Scene(loginPage.getLoginLayout(), 400, 300));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while navigating back.", Alert.AlertType.ERROR);
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