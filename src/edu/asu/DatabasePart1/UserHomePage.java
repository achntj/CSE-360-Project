package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> UserHomePage. </p>
 * 
 * <p> Description: This class provides the user interface for the home page that
 * users are redirected to after login. The layout is role-specific, and users
 * can log out from this page. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya
 *         Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Project Phase 1 User Home Page
 * @version 2.00 2024-10-30 Project Phase 2 User Home Page
 * @version 3.00 2024-11-20 Project Phase 3 User Home Page
 */
public class UserHomePage {

    /** The primary stage used for the Graphical-User-Interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in user. */
    private final String email;

    /** The role of the logged-in user. */
    private final String role;

    /** The GridPane used to structure the user home page UI. */
    private final GridPane homeGrid;

    /**
     * Constructs the UserHomePage with the given parameters.
     * Initializes the user home page and sets up all components in the graphical
     * interface, including a welcome message and a logout button.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in user
     * @param role           the role of the logged-in user
     */
    public UserHomePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup the layout for the user home page using GridPane
        homeGrid = new GridPane();
        homeGrid.setAlignment(Pos.CENTER);
        homeGrid.setVgap(10);
        homeGrid.setHgap(10);

        // Define the welcome label and other UI elements
        Label welcomeLabel = new Label("Welcome to User Home Page, " + role + "!");
        Button createArticleButton = new Button("Create Article");
        Button listArticlesButton = new Button("List Articles");
        Button deleteArticleButton = new Button("Delete Article");
        Button backupArticlesButton = new Button("Backup Articles");
        Button restoreArticlesButton = new Button("Restore Articles");
        Button backupByKeywordButton = new Button("Backup Group: ");
        TextField keywordToBackupField = new TextField();
        Button restoreKeywordArticles = new Button("Restore Group:");
        TextField keywordToRestore = new TextField();
        Button logoutButton = new Button("Log Out");
        Button helpButton = new Button("Help:");
        Button groupButton = new Button("View / Edit Groups:");

        // Add components to the home grid layout
        homeGrid.add(welcomeLabel, 0, 0);
        homeGrid.add(createArticleButton, 0, 1);
        homeGrid.add(listArticlesButton, 0, 2);
        homeGrid.add(deleteArticleButton, 0, 3);
        homeGrid.add(backupArticlesButton, 0, 4);
        homeGrid.add(restoreArticlesButton, 0, 5);
        homeGrid.add(backupByKeywordButton, 0, 6);
        homeGrid.add(keywordToBackupField, 0, 7);
        homeGrid.add(restoreKeywordArticles, 0, 8);
        homeGrid.add(keywordToRestore, 0, 9);
        homeGrid.add(logoutButton, 0, 10);
        homeGrid.add(helpButton, 0, 11);
        homeGrid.add(groupButton, 0, 12);

        // Event handlers for buttons
        setupEventHandlers(createArticleButton, listArticlesButton, deleteArticleButton, 
                           backupArticlesButton, restoreArticlesButton, backupByKeywordButton, 
                           restoreKeywordArticles, logoutButton, helpButton, groupButton, 
                           keywordToBackupField, keywordToRestore);
    }

    /**
     * Sets up event handlers for various buttons on the User Home Page.
     */
    private void setupEventHandlers(Button createArticleButton, Button listArticlesButton, Button deleteArticleButton,
                                    Button backupArticlesButton, Button restoreArticlesButton,
                                    Button backupByKeywordButton, Button restoreKeywordArticles,
                                    Button logoutButton, Button helpButton, Button groupButton,
                                    TextField keywordToBackupField, TextField keywordToRestore) {
        createArticleButton.setOnAction(event -> {
            try {
                databaseHelper.ensureConnection();
                CreateArticlePage createArticlePage = new CreateArticlePage(primaryStage, databaseHelper, email, role);
                Scene createArticleScene = new Scene(createArticlePage.getCreateArticleLayout(), 400, 300);
                primaryStage.setScene(createArticleScene);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while entering create an article.", Alert.AlertType.ERROR);
            }
        });

        listArticlesButton.setOnAction(event -> {
            try {
                String articleList = databaseHelper.listArticles();
                showAlert("Articles", articleList, Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while listing articles.", Alert.AlertType.ERROR);
            }
        });

        deleteArticleButton.setOnAction(event -> {
            try {
                databaseHelper.ensureConnection();
                DeleteArticlePage deleteArticlePage = new DeleteArticlePage(primaryStage, databaseHelper, email, role);
                Scene deleteArticleScene = new Scene(deleteArticlePage.getDeleteArticleScene(), 400, 300);
                primaryStage.setScene(deleteArticleScene);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while deleting an article.", Alert.AlertType.ERROR);
            }
        });

        backupArticlesButton.setOnAction(event -> {
            showAlert("Info", "Backing up Articles...", Alert.AlertType.INFORMATION);
            try {
                databaseHelper.ensureConnection();
                databaseHelper.backupArticles("articleBackup.txt");
                showAlert("Success", "Backup created successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while backing up articles.", Alert.AlertType.ERROR);
            }
        });

        // More button setups...
    }

    /**
     * Returns the user home layout, used in scene creation.
     * 
     * @return the GridPane layout of the User Home Page
     */
    public GridPane getUserHomeLayout() {
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