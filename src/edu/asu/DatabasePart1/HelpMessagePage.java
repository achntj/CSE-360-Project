package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> HelpMessagePage. </p>
 * 
 * <p> Description: This class provides the user interface for sending help 
 * messages to the system. Users can send either generic or specific messages 
 * based on their requirements. The page also includes navigation back to the 
 * respective user's home page. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * <p> This page is accessible to all users regardless of their role. </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-11-18 Project Phase 3 Help Message Page
 */
public class HelpMessagePage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** Handles database operations such as sending messages. */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in user. */
    private final String email;

    /** The role of the logged-in user. */
    private final String role;

    /** The GridPane used to structure the help message page UI. */
    private final GridPane messageGrid;

    /**
     * Constructs the HelpMessagePage with the given parameters.
     * Initializes the user interface for sending help messages and navigating back 
     * to the user's respective home page. Handles interactions and error scenarios.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in user
     * @param role           the role of the logged-in user
     */
    public HelpMessagePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Creates a new GridPane and sets the alignment and spacing
        messageGrid = new GridPane();
        messageGrid.setAlignment(Pos.CENTER);
        messageGrid.setVgap(10);
        messageGrid.setHgap(10);

        // Define UI components
        TextField genericMessageField = new TextField();
        Button sendGeneric = new Button("Send Generic Message");
        TextField specificMessageField = new TextField();
        Button sendSpecific = new Button("Send Specific Message");
        Button backButton = new Button("Back");

        // Add components to the GridPane layout
        messageGrid.add(genericMessageField, 0, 0);
        messageGrid.add(sendGeneric, 1, 0);
        messageGrid.add(specificMessageField, 0, 1);
        messageGrid.add(sendSpecific, 1, 1);
        messageGrid.add(backButton, 0, 2);

        // Set button actions
        sendGeneric.setOnAction(event -> handleSendMessage(genericMessageField.getText().trim(), "generic"));
        sendSpecific.setOnAction(event -> handleSendMessage(specificMessageField.getText().trim(), "specific"));
        backButton.setOnAction(event -> navigateBack());
    }

    /**
     * Sends a help message to the system.
     * Validates the message content, sends it to the database, and displays a 
     * success or error message.
     * 
     * @param message the message content entered by the user
     * @param type    the type of message ("generic" or "specific")
     */
    private void handleSendMessage(String message, String type) {
        // Validate message content
        if (message.isEmpty()) {
            showAlert("Error", "Message must be provided.", Alert.AlertType.ERROR);
            return;
        }

        // Attempt to send the message
        try {
            databaseHelper.sendMessage(message, email, type);
            showAlert("Success", "Message sent successfully.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while sending the message.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Navigates back to the user's respective home page based on their role.
     */
    private void navigateBack() {
        try {
            switch (role.toLowerCase()) {
                case "admin":
                    AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
                    primaryStage.setScene(new Scene(adminHomePage.getAdminHomeLayout(), 400, 300));
                    break;

                case "instructor":
                    InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper, email, "instructor");
                    primaryStage.setScene(new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300));
                    break;

                case "student":
                    StudentHomePage studentHomePage = new StudentHomePage(primaryStage, databaseHelper, email, "student");
                    primaryStage.setScene(new Scene(studentHomePage.getStudentHomeLayout(), 400, 300));
                    break;

                default:
                    // Logout and redirect to login page if the role is unrecognized
                    databaseHelper.ensureConnection();
                    showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);
                    LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                    primaryStage.setScene(new Scene(loginPage.getLoginLayout(), 400, 300));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while navigating.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Returns the help message layout, used in scene creation.
     * 
     * @return the GridPane layout of the Help Message Page
     */
    public GridPane getHelpMessageLayout() {
        return messageGrid;
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