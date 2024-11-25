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
 * <p> Description: This class provides the user interface for inviting new 
 * users to the system. Admin users can specify roles and generate unique 
 * invitation codes tied to those roles, which can be used during the registration 
 * process. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * <p> This page is accessible only to admin users for user invitation purposes. </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 */
public class InviteUserPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /**
     * Handles database operations such as generating invitation codes and 
     * updating user roles.
     */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in admin user. */
    private final String email;

    /** The GridPane used to structure the invite user page UI. */
    private final GridPane inviteUserGrid;

    /**
     * Constructs the InviteUserPage with the given parameters.
     * Initializes the user interface for generating invitation codes, allowing 
     * admin users to assign roles and manage user invitations.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in admin user
     */
    public InviteUserPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Creates a new GridPane and sets its alignment and spacing
        inviteUserGrid = new GridPane();
        inviteUserGrid.setAlignment(Pos.CENTER);
        inviteUserGrid.setVgap(10);
        inviteUserGrid.setHgap(10);

        // Establishes text fields and buttons used in the user interface
        Label roleLabel = new Label("Role(s):");
        TextField roleField = new TextField();
        Button generateCodeButton = new Button("Generate Invitation Code");
        Button backButton = new Button("Back");

        // Adds the fields and buttons to the interface
        inviteUserGrid.add(roleLabel, 0, 0);
        inviteUserGrid.add(roleField, 1, 0);
        inviteUserGrid.add(generateCodeButton, 1, 1);
        inviteUserGrid.add(backButton, 1, 2);

        // Adds functionality for the 'Generate Invitation Code' button
        generateCodeButton.setOnAction(event -> handleGenerateCode(roleField));

        // Adds functionality for the 'Back' button to return to the admin home page
        backButton.setOnAction(event -> navigateBackToAdminHomePage());
    }

    /**
     * Handles the generation of an invitation code.
     * Validates the entered role(s), generates a unique invitation code tied 
     * to the specified roles, and displays the generated code to the user.
     * 
     * @param roleField the text field where the admin specifies the roles for the invitation
     */
    private void handleGenerateCode(TextField roleField) {
        // Retrieves the entered role(s) to associate with the invitation code
        String roles = roleField.getText().trim();

        // If no roles are specified, displays an error message
        if (roles.isEmpty()) {
            showAlert("Error", "Role(s) must be specified.", Alert.AlertType.ERROR);
            return;
        }

        // Generates the invitation code for the specified roles and alerts the user
        // upon success or failure
        try {
            String invitationCode = databaseHelper.generateInvitationCode(roles);
            showAlert("Success", "Invitation code generated: " + invitationCode, Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while generating the invitation code.",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Navigates back to the Admin Home Page.
     * Redirects the admin user to the Admin Home Page when the 'Back' button is clicked.
     */
    private void navigateBackToAdminHomePage() {
        AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
        Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
        primaryStage.setScene(adminScene);
    }

    /**
     * Returns the invite user layout, used in scene creation.
     * 
     * @return the GridPane layout of the Invite User Page
     */
    public GridPane getInviteUserLayout() {
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