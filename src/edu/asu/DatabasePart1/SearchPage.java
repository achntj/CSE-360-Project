package edu.asu.DatabasePart1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p>
 * SearchPage.
 * </p>
 * 
 * <p>
 * Description: This class provides the user interface for searching articles
 * with filters for difficulty levels and group associations. Users can also
 * navigate to other pages or log out from this page.
 * </p>
 * 
 * <p>
 * Copyright: Group 11 - CSE 360 © 2024
 * </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya
 *         Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 * @version 2.00 2024-10-30 Updated for Phase 2
 * @version 3.00 2024-11-20 Updated for Phase 3
 */
public class SearchPage {

	/** The primary stage used for the Graphical-User-Interface. */
	private final Stage primaryStage;

	/** The database helper that allows interactions with the user database. */
	private final DatabaseHelper databaseHelper;

	/** The email of the logged-in user. */
	private final String email;

	/** The role of the logged-in user. */
	private final String role;

	/** The GridPane used to structure the search page UI. */
	private final GridPane homeGrid;

	/** The list of groups accessible to the logged-in user. */
	private String[] groups;

	/**
	 * Constructs the SearchPage with the given parameters. Initializes the search
	 * page and sets up all components in the graphical interface, including search
	 * filters, buttons for navigation, and a logout option.
	 * 
	 * @param primaryStage   the primary stage used to display the graphical
	 *                       interface
	 * @param databaseHelper the database helper that enables interaction with the
	 *                       database
	 * @param email          the email of the logged-in user
	 * @param role           the role of the logged-in user
	 */
	public SearchPage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
		this.primaryStage = primaryStage;
		this.databaseHelper = databaseHelper;
		this.email = email;
		this.role = role;
		this.groups = null;

		// Setup the layout for the search page using GridPane
		homeGrid = new GridPane();
		homeGrid.setAlignment(Pos.CENTER);
		homeGrid.setVgap(10);
		homeGrid.setHgap(10);

		// Define the search UI components
		Button searchButton = new Button("Search: ");
		TextField searchCriteria = new TextField();

		Label filterDifficultyLabel = new Label("Filter Article Difficulty: ");
		ToggleGroup difficultyToggle = new ToggleGroup();

		Label filterGroupsLabel = new Label("Filter Groups Displayed: ");
		ToggleGroup groupsToggle = new ToggleGroup();

		Button logoutButton = new Button("Log Out");
		Button backButton = new Button("Back");
		Button helpButton = new Button("Help");

		RadioButton beginnerLevels = new RadioButton("Beginner");
		RadioButton intermediateLevels = new RadioButton("Intermediate");
		RadioButton advancedLevels = new RadioButton("Advanced");
		RadioButton expertLevels = new RadioButton("Expert");

		// Add components to the home grid layout
		homeGrid.add(searchButton, 0, 0);
		homeGrid.add(searchCriteria, 2, 0);

		homeGrid.add(filterDifficultyLabel, 0, 1);
		homeGrid.add(filterGroupsLabel, 2, 1);

		beginnerLevels.setToggleGroup(difficultyToggle);
		intermediateLevels.setToggleGroup(difficultyToggle);
		advancedLevels.setToggleGroup(difficultyToggle);
		expertLevels.setToggleGroup(difficultyToggle);

		homeGrid.add(beginnerLevels, 0, 2);
		homeGrid.add(intermediateLevels, 0, 3);
		homeGrid.add(advancedLevels, 0, 4);
		homeGrid.add(expertLevels, 0, 5);

		int lastLayer = 6;
		int row = 2;

		// Load accessible groups for the user
		try {
			groups = databaseHelper.groupNamesAccessible(email);
		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Database Error", "An error occurred while retrieving accessible groups.", Alert.AlertType.ERROR);
		}

		if (groups == null) {
			Label noGroupsLabel = new Label("No Groups Available!");
			homeGrid.add(noGroupsLabel, 2, row);
		} else {
			for (String group : groups) {
				RadioButton groupOption = new RadioButton(group);
				groupOption.setToggleGroup(groupsToggle);
				homeGrid.add(groupOption, 2, row++);
			}
			if (lastLayer < row) {
				lastLayer = row;
			}
		}

		homeGrid.add(logoutButton, 1, lastLayer);
		homeGrid.add(backButton, 0, lastLayer);
		homeGrid.add(helpButton, 2, lastLayer);

		// Set up search button action
		searchButton.setOnAction(event -> {
			System.out.println("Attempting to Perform Search...");
			
			String searchQuery = searchCriteria.getText().trim();

			RadioButton selectedGroup = (RadioButton) groupsToggle.getSelectedToggle();
			String groupFilter;
			if (selectedGroup == null) {
				groupFilter = "ALL";
			} else {
				groupFilter = selectedGroup.getText().trim();
			}

			RadioButton selectedDifficulty = (RadioButton) difficultyToggle.getSelectedToggle();
			String difficultyFilter = null;
			if (selectedDifficulty == null) {
				difficultyFilter = "ALL";
			} else {
				difficultyFilter = selectedDifficulty.getText().trim().toLowerCase();
			}

			int[] filteredArticleList = null;
			String listOfArticles = null;
			int numberOfArticlesUnderSearch = 0;

			try {
				filteredArticleList = databaseHelper.getGroupsAccessibleIDs(email);

				if (filteredArticleList != null) {

					if (groupFilter.equalsIgnoreCase("ALL")) {
						filteredArticleList = databaseHelper.getArticlesFromGroupProvided(filteredArticleList, "*");
					} else {
						filteredArticleList = databaseHelper.getArticlesFromGroupProvided(filteredArticleList,
								groupFilter);
					}

				}

				if (filteredArticleList != null) {

					if (difficultyFilter.equalsIgnoreCase("ALL")) {
						filteredArticleList = databaseHelper.articlesFilteredDifficulty(filteredArticleList, "*");
					} else {
						filteredArticleList = databaseHelper.articlesFilteredDifficulty(filteredArticleList,
								difficultyFilter);
					}
				}
				
				if (filteredArticleList != null) {
					filteredArticleList = databaseHelper.searchForArticles(searchQuery, filteredArticleList);
				}

				try {

					if (filteredArticleList == null) {
						listOfArticles = "No Articles Found!";
					} else {
						listOfArticles = databaseHelper.getArticlesFromList(filteredArticleList);
						numberOfArticlesUnderSearch = filteredArticleList.length;
					}
				} catch (Exception e) {
					e.printStackTrace();
					showAlert("Error", "An error occurred retrieving articles.", Alert.AlertType.ERROR);
				}

			} catch (Exception e) {
				e.printStackTrace();
				showAlert("Error", "An error occurred while filtering the search.", Alert.AlertType.ERROR);
			}

			SearchDisplayPage searchDisplayPage = new SearchDisplayPage(primaryStage, databaseHelper, email, role,
					listOfArticles, numberOfArticlesUnderSearch, groupFilter, filteredArticleList);
			Scene searchDisplayScene = new Scene(searchDisplayPage.getSearchLayout(), 400, 300);
			primaryStage.setScene(searchDisplayScene);

		});

		// Set up logout button action
		logoutButton.setOnAction(event -> {
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
		});

		// Set up back button action
		backButton.setOnAction(event -> {
			if (role.equalsIgnoreCase("admin")) {
				AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
				Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
				primaryStage.setScene(adminScene);
			} else if (role.equalsIgnoreCase("instructor")) {
				InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper, email,
						"instructor");
				Scene instructorScene = new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300);
				primaryStage.setScene(instructorScene);
			} else {
				showAlert("Error", "Invalid role for navigation.", Alert.AlertType.ERROR);
			}
		});
	}

	/**
	 * Returns the search page layout, used in scene creation.
	 * 
	 * @return the GridPane layout of the Search Page
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