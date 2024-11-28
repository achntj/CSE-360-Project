package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> DeleteUserAccountPage. </p>
 * 
 * <p> Description: This class provides an interactive JavaFX interface 
 * that allows administrators to remove user accounts from the database.
 * It includes features for validating user input, confirming actions,
 * and handling database interactions with error management. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Project Phase 1 Delete User Account Page
 */
public class DeleteUserAccountPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** The database helper that enables interaction with the database. */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in user. */
    private final String email;

    /** The GridPane used to structure the delete user account page UI. */
    private final GridPane deleteUserGrid;

    /**
     * Constructs the DeleteUserAccountPage with the given parameters.
     * Initializes the graphical interface for deleting user accounts and 
     * manages interactions with the database.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in user
     */
    public DeleteUserAccountPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup the layout for the delete user account page using GridPane
        deleteUserGrid = new GridPane();
        deleteUserGrid.setAlignment(Pos.CENTER);
        deleteUserGrid.setVgap(10);
        deleteUserGrid.setHgap(10);

        // Define UI components
        Label emailLabel = new Label("User Email:");
        TextField emailField = new TextField();
        Button deleteUserButton = new Button("Delete User Account");
        Button backButton = new Button("Back");

        // Add components to the GridPane layout
        deleteUserGrid.add(emailLabel, 0, 0);
        deleteUserGrid.add(emailField, 1, 0);
        deleteUserGrid.add(deleteUserButton, 1, 1);
        deleteUserGrid.add(backButton, 1, 2);

        // Set action for the 'Delete User Account' button
        deleteUserButton.setOnAction(event -> handleDeleteUser(emailField.getText().trim()));

        // Set action for the 'Back' button
        backButton.setOnAction(event -> navigateToAdminHomePage());
    }

    /**
     * Handles the process of deleting a user account.
     * Validates the input email, confirms the action, and interacts with the database.
     * 
     * @param userEmail the email of the user account to be deleted
     */
    private void handleDeleteUser(String userEmail) {
        if (userEmail.isEmpty()) {
            showAlert("Error", "Email must be provided.", Alert.AlertType.ERROR);
            return;
        }

        // Show confirmation alert
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete this user?", 
                ButtonType.YES, ButtonType.NO);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.showAndWait();

        if (confirmation.getResult() == ButtonType.YES) {
            try {
                databaseHelper.deleteUserAccount(userEmail);
                showAlert("Success", "User account deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while deleting the user account.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Navigates back to the Admin Home Page.
     * Sets the scene to the Admin Home Page layout.
     */
    private void navigateToAdminHomePage() {
        AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
        Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
        primaryStage.setScene(adminScene);
    }

    /**
     * Returns the layout of the delete user account page, used in scene creation.
     * 
     * @return the GridPane layout of the DeleteUserAccountPage
     */
    public GridPane getDeleteUserLayout() {
        return deleteUserGrid;
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