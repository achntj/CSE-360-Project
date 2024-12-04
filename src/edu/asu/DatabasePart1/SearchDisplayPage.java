package edu.asu.DatabasePart1;

import java.sql.SQLException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> SearchDisplayPage. </p>
 * 
 * <p> Description: This class provides the user interface for displaying the
 * results of a search operation. It shows details like the active group, number
 * of articles found, and the search content. Users can navigate back to the
 * search page or view an individual article by its ID. </p>
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
public class SearchDisplayPage {

    /** The primary stage used for the Graphical-User-Interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The email of the logged-in user. */
    private final String email;

    /** The role of the logged-in user. */
    private final String role;

    /** The GridPane used to structure the search display page UI. */
    private final GridPane homeGrid;

    /**
     * Constructs the SearchDisplayPage with the given parameters.
     * Initializes the search display page and sets up all components in the 
     * graphical interface, including labels for group and article details, a 
     * list of articles, and buttons for navigation.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the logged-in user
     * @param role           the role of the logged-in user
     * @param searchContent  the content of the search results to be displayed
     * @param articleNumbers the total number of articles found in the search
     * @param group          the currently active group for the search
     * @param articleList    the list of article IDs resulting from the search
     */
    public SearchDisplayPage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role,
            String searchContent, int articleNumbers, String group, int[] articleList) {

        // Initializes the primaryStage, database helper, email, and role
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup the layout for the search display page using GridPane
        homeGrid = new GridPane();
        homeGrid.setAlignment(Pos.CENTER);
        homeGrid.setVgap(10);
        homeGrid.setHgap(10);

        // Define UI components for the search display
        Label activeGroups = new Label("Currently Active Group: " + group);
        Label numberOfArticles = new Label("Number of Articles: " + articleNumbers);

        Label listedArticles = new Label(searchContent);
        listedArticles.setWrapText(true);

        Button backToSearchButton = new Button("Back To Search Page");
        Button displayArticleButton = new Button("View Article (ENTER ID): ");
        TextField articleID = new TextField();

        // Add components to the home grid layout
        homeGrid.add(activeGroups, 0, 0);
        homeGrid.add(numberOfArticles, 1, 0);

        homeGrid.add(listedArticles, 0, 2);

        homeGrid.add(displayArticleButton, 0, 3);
        homeGrid.add(backToSearchButton, 0, 4);
        homeGrid.add(articleID, 1, 3);

        // Set up the back-to-search button action
        backToSearchButton.setOnAction(event -> {
            SearchPage searchPage = new SearchPage(primaryStage, databaseHelper, email, role);
            Scene searchScene = new Scene(searchPage.getSearchLayout(), 400, 300);
            primaryStage.setScene(searchScene);
        });

        // Set up the view-article button action
        displayArticleButton.setOnAction(event -> {
            String idString = articleID.getText().trim();
            if (!idString.isBlank()) {
                try {
                    int id = Integer.parseInt(idString);
                    String article = databaseHelper.displayFullArticle(id);
                    showAlert("Article ID: " + id, article, Alert.AlertType.INFORMATION);
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter a valid numeric ID!", Alert.AlertType.ERROR);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "Not a valid ID!", Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Error!", "Please provide an article ID!", Alert.AlertType.ERROR);
            }
        });
    }

    /**
     * Returns the search display layout, used in scene creation.
     * 
     * @return the GridPane layout of the Search Display Page
     */
    public GridPane getSearchLayout() {
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