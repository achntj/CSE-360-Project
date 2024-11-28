package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> StudentHomePage. </p>
 * 
 * <p> Description: This class provides the user interface for the home page 
 * specifically for students. It allows students to perform actions such as 
 * listing articles, searching, accessing help, and logging out. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 * @version 2.00 2024-10-30 Updated for Phase 2
 * @version 3.00 2024-11-20 Updated for Phase 3
 */
public class StudentHomePage {

    /** The primary stage used for the Graphical-User-Interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in student. */
    private final String email;

    /** The role of the logged-in student. */
    private final String role;

    /** The GridPane used to structure the student home page UI. */
    private final GridPane homeGrid;

    /**
     * Constructs the StudentHomePage with the given parameters.
     * Initializes the student home page and sets up all components in the 
     * graphical interface, including a welcome message and various action buttons.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in student
     * @param role           the role of the logged-in student
     */
    public StudentHomePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup the layout for the student home page using GridPane
        homeGrid = new GridPane();
        homeGrid.setAlignment(Pos.CENTER);
        homeGrid.setVgap(10);
        homeGrid.setHgap(10);

        // Define the welcome label and action buttons
        Label welcomeLabel = new Label("Welcome to User Home Page, " + role + "!");
        Button listArticlesButton = new Button("List Articles");
        Button searchButton = new Button("Search");
        Button logoutButton = new Button("Log Out");
        Button helpButton = new Button("Help:");

        // Add components to the home grid layout
        homeGrid.add(welcomeLabel, 0, 0);
        homeGrid.add(listArticlesButton, 0, 1);
        homeGrid.add(searchButton, 0, 2);
        homeGrid.add(logoutButton, 0, 3);
        homeGrid.add(helpButton, 0, 4);

        // Set up action events for the buttons
        listArticlesButton.setOnAction(event -> {
            try {
                // Display a list of articles
                String articleList = databaseHelper.listArticles();
                showAlert("Articles", articleList, Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while listing articles.", Alert.AlertType.ERROR);
            }
        });

        logoutButton.setOnAction(event -> {
            try {
                // Log out and redirect to the login page
                databaseHelper.ensureConnection();
                showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                primaryStage.setScene(loginScene);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred during logout.", Alert.AlertType.ERROR);
            }
        });

        helpButton.setOnAction(event -> {
            // Redirect to the help page
            HelpMessagePage helpMessagePage = new HelpMessagePage(primaryStage, databaseHelper, email, role);
            Scene helpMessageScene = new Scene(helpMessagePage.getHelpMessageLayout(), 400, 300);
            primaryStage.setScene(helpMessageScene);
        });

        searchButton.setOnAction(event -> {
            // Redirect to the search page
            SearchPage searchPage = new SearchPage(primaryStage, databaseHelper, email, role);
            Scene searchScene = new Scene(searchPage.getSearchLayout(), 400, 300);
            primaryStage.setScene(searchScene);
        });
    }

    /**
     * Returns the student home layout, used in scene creation.
     * 
     * @return the GridPane layout of the Student Home Page
     */
    public GridPane getStudentHomeLayout() {
        return homeGrid;
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