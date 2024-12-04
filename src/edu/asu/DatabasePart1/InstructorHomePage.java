package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> InstructorHomePage. </p>
 * 
 * <p> Description: This class provides the user interface for the instructor's 
 * home page. It allows instructors to perform actions such as managing students, 
 * searching articles, and managing article groups. The layout and actions are 
 * tailored to the instructor role. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * <p> This page is accessible only to users with the "instructor" role. </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 * @version 2.00 2024-10-30 Updated for Phase 2
 */
public class InstructorHomePage {

    /** The primary stage used for the graphical user interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in instructor. */
    private final String email;

    /** The role of the logged-in user, which should be "instructor". */
    private final String role;

    /** The GridPane used to structure the instructor home page UI. */
    private final GridPane homeGrid;

    /**
     * Constructs the InstructorHomePage with the given parameters.
     * Initializes the home page and sets up all components in the graphical 
     * interface, including buttons for managing students, searching articles, 
     * and logging out.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in instructor
     * @param role           the role of the logged-in user
     */
    public InstructorHomePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup the layout for the instructor home page using GridPane
        homeGrid = new GridPane();
        homeGrid.setAlignment(Pos.CENTER);
        homeGrid.setVgap(10);
        homeGrid.setHgap(10);

        String usedName = "User";
        try {
            databaseHelper.ensureConnection();
            usedName = databaseHelper.getName(email);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while accessing the user's name.", Alert.AlertType.ERROR);
        }

        // Define the welcome label and other buttons
        Label welcomeLabel = new Label("Welcome to Instructor Home, " + usedName + "!");
        Button addStudentButton = new Button("Add Student");
        Button deleteStudentButton = new Button("Delete Student");
        Button listStudentsButton = new Button("List Students");
        Button searchArticlesButton = new Button("Search Articles");
        Button articleFunctionsButton = new Button("Article Functions");
        Button articleGroupsButton = new Button("Article Group Functions");
        Button logoutButton = new Button("Log Out");
        Button helpButton = new Button("Help");

        // Add components to the home grid layout
        homeGrid.add(welcomeLabel, 0, 0);
        homeGrid.add(addStudentButton, 0, 1);
        homeGrid.add(deleteStudentButton, 2, 1);
        homeGrid.add(listStudentsButton, 0, 2);
        homeGrid.add(searchArticlesButton, 2, 2);
        homeGrid.add(articleFunctionsButton, 0, 3);
        homeGrid.add(articleGroupsButton, 2, 3);
        homeGrid.add(logoutButton, 0, 4);
        homeGrid.add(helpButton, 2, 4);

        // Set up button actions
        addStudentButton.setOnAction(event -> navigateToAddStudentPage());
        deleteStudentButton.setOnAction(event -> navigateToDeleteStudentPage());
        listStudentsButton.setOnAction(event -> listStudents());
        searchArticlesButton.setOnAction(event -> navigateToSearchArticlesPage());
        articleFunctionsButton.setOnAction(event -> navigateToArticleFunctionsPage());
        articleGroupsButton.setOnAction(event -> navigateToArticleGroupsPage());
        logoutButton.setOnAction(event -> handleLogout());
        helpButton.setOnAction(event -> navigateToHelpPage());
    }

    /**
     * Returns the instructor home layout, used in scene creation.
     * 
     * @return the GridPane layout of the Instructor Home Page
     */
    public GridPane getInstructorHomeLayout() {
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

    // Navigation methods for button actions

    private void navigateToAddStudentPage() {
        AddStudentPage addStudentPage = new AddStudentPage(primaryStage, databaseHelper, email, "instructor");
        Scene addStudentScene = new Scene(addStudentPage.getAddUserLayout(), 400, 300);
        primaryStage.setScene(addStudentScene);
    }

    private void navigateToDeleteStudentPage() {
        DeleteStudentPage deleteStudentPage = new DeleteStudentPage(primaryStage, databaseHelper, email, "instructor");
        Scene deleteStudentScene = new Scene(deleteStudentPage.getDeleteStudentLayout(), 400, 300);
        primaryStage.setScene(deleteStudentScene);
    }

    private void listStudents() {
        try { 
            String studentList = databaseHelper.getStudentList();
            showAlert("Students", studentList, Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while listing students.", Alert.AlertType.ERROR);
        }
    }

    private void navigateToSearchArticlesPage() {
        SearchPage searchPage = new SearchPage(primaryStage, databaseHelper, email, "instructor");
        Scene searchScene = new Scene(searchPage.getSearchLayout(), 400, 300);
        primaryStage.setScene(searchScene);
    }

    private void navigateToArticleFunctionsPage() {
        ArticleFunctionsPage articleFunctionsPage = new ArticleFunctionsPage(primaryStage, databaseHelper, email,
                "instructor");
        Scene articleFunctionsScene = new Scene(articleFunctionsPage.getArticleFunctionsLayout(), 400, 300);
        primaryStage.setScene(articleFunctionsScene);
    }

    private void navigateToArticleGroupsPage() {
        try {
            databaseHelper.ensureConnection();
            String id = databaseHelper.getUserIdFromEmail(email);
            GroupAccessPage groupAccessPage = new GroupAccessPage(primaryStage, databaseHelper, id, email,
                    "instructor");
            Scene groupAccessScene = new Scene(groupAccessPage.getGroupAccessLayout(), 400, 300);
            primaryStage.setScene(groupAccessScene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "No user ID found for the current email.", Alert.AlertType.ERROR);
        }
    }

    private void handleLogout() {
        try {
            databaseHelper.ensureConnection();
            showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);
            LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
            Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
            primaryStage.setScene(loginScene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during logout.", Alert.AlertType.ERROR);
        }
    }

    private void navigateToHelpPage() {
        HelpMessagePage helpMessagePage = new HelpMessagePage(primaryStage, databaseHelper, email, "instructor");
        Scene helpMessageScene = new Scene(helpMessagePage.getHelpMessageLayout(), 400, 300);
        primaryStage.setScene(helpMessageScene);
    }
}