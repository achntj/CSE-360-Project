package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p>
 * CreateArticlePage.
 * </p>
 * 
 * <p>
 * Description: This class represents the UI for creating a new article entry.
 * It provides fields for the article's title, difficulty level, authors,
 * abstract, keywords, body, and references. Users can also return to the
 * homepage from this page.
 * </p>
 * 
 * <p>
 * Copyright: Group 11 - CSE 360 © 2024
 * </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya
 *         Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-30 Project Phase 2 User Home Page
 */

public class CreateArticlePage {

	/** The primary stage used for the Graphical-User-Interface */
	private final Stage primaryStage;

	/** The database helper that allows interactions with the article database */
	private final DatabaseHelper databaseHelper;

	/** The email of the logged-in user */
	private final String email;

	/** The Grid Pane used to structure the create article UI */
	private final GridPane createArticleGrid;

	/**
	 * Constructor that initializes the create article page and sets up all
	 * components in the UI.
	 * 
	 * @param primaryStage   The primary stage used to display the graphical
	 *                       interface
	 * @param databaseHelper The database helper enabling interaction with the
	 *                       database
	 * @param email          The email of the logged-in user
	 * @param role           The role of the logged-in user
	 */
	public CreateArticlePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
		this.primaryStage = primaryStage;
		this.databaseHelper = databaseHelper;
		this.email = email;

		// Setup layout for the create article page using GridPane
		createArticleGrid = new GridPane();
		createArticleGrid.setAlignment(Pos.CENTER);
		createArticleGrid.setVgap(10);
		createArticleGrid.setHgap(10);

		// Define labels and input fields for article details
		Label titleLabel = new Label("Title: ");
		TextField titleTextField = new TextField();

		Label difficultyLabel = new Label("Difficulty Level:");
		ComboBox<String> difficultyComboBox = new ComboBox<>();
		difficultyComboBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
		difficultyComboBox.setValue("Beginner"); // Set default value

		Label authorsLabel = new Label("Authors:");
		TextField authorsTextField = new TextField();

		Label abstractLabel = new Label("Abstract:");
		TextField abstractTextField = new TextField();

		Label keywordsLabel = new Label("Keywords:");
		TextField keywordsField = new TextField();

		Label bodyLabel = new Label("Body:");
		TextField bodyField = new TextField();

		Label referenceLinksLabel = new Label("References:");
		TextField referenceLinksField = new TextField();

		Button createArticleButton = new Button("Complete Article Setup");
		Button backButton = new Button("Back");

		// Add components to the create article grid layout
		createArticleGrid.add(titleLabel, 0, 0);
		createArticleGrid.add(titleTextField, 1, 0);

		createArticleGrid.add(difficultyLabel, 0, 1);
		createArticleGrid.add(difficultyComboBox, 1, 1);

		createArticleGrid.add(authorsLabel, 0, 2);
		createArticleGrid.add(authorsTextField, 1, 2);

		createArticleGrid.add(abstractLabel, 0, 3);
		createArticleGrid.add(abstractTextField, 1, 3);

		createArticleGrid.add(keywordsLabel, 0, 4);
		createArticleGrid.add(keywordsField, 1, 4);

		createArticleGrid.add(bodyLabel, 0, 5);
		createArticleGrid.add(bodyField, 1, 5);

		createArticleGrid.add(referenceLinksLabel, 0, 6);
		createArticleGrid.add(referenceLinksField, 1, 6);

		createArticleGrid.add(backButton, 0, 7);
		createArticleGrid.add(createArticleButton, 1, 7);

		// Action event for create article button
		createArticleButton.setOnAction(event -> {
			String title = titleTextField.getText().trim();
			String difficulty = difficultyComboBox.getValue().trim();
			String authors = authorsTextField.getText().trim();
			String abstractVal = abstractTextField.getText().trim();
			String keywords = keywordsField.getText().trim();
			String body = bodyField.getText().trim();
			String references = referenceLinksField.getText().trim();

			// Check for empty fields and notify user if found
			if (title.isEmpty() || authors.isEmpty() || abstractVal.isEmpty() || keywords.isEmpty() || body.isEmpty()
					|| references.isEmpty()) {
				showAlert("Error", "Empty text fields, please fill out all categories!", Alert.AlertType.ERROR);
				return;
			}

			// Attempt to create the article and handle exceptions if they occur
			try {
				databaseHelper.createArticle(null, title, difficulty, authors, abstractVal, keywords, body, references);
				showAlert("Success", "Article Added Successfully!", Alert.AlertType.INFORMATION);

				// Redirect to user home page after article creation

				if (role.equalsIgnoreCase("admin")) {
					AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
					Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
					primaryStage.setScene(adminScene);
				}

				else if (role.equalsIgnoreCase("instructor")) {
					InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper, email,
							role);
					Scene instructorScene = new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300);
					primaryStage.setScene(instructorScene);
				}

			} catch (Exception e) {
				e.printStackTrace();
				showAlert("Database Error", "An error occurred while adding the article.", Alert.AlertType.ERROR);
			}

		});

		// Action event for back button to return to home page
		backButton.setOnAction(event -> {
			if (role.equalsIgnoreCase("admin")) {
				ArticleFunctionsPage articleFunctionsPage = new ArticleFunctionsPage(primaryStage, databaseHelper,
						email, "admin");
				Scene articleFunctionsScene = new Scene(articleFunctionsPage.getArticleFunctionsLayout(), 400, 300);
				primaryStage.setScene(articleFunctionsScene);
			}

			else if (role.equalsIgnoreCase("instructor")) {
				ArticleFunctionsPage articleFunctionsPage = new ArticleFunctionsPage(primaryStage, databaseHelper,
						email, "instructor");
				Scene articleFunctionsScene = new Scene(articleFunctionsPage.getArticleFunctionsLayout(), 400, 300);
				primaryStage.setScene(articleFunctionsScene);
			} else {

				try {
					// Ensure connection to the database and log the user out
					databaseHelper.ensureConnection();
					showAlert("Logout", "You have been logged out successfully.", Alert.AlertType.INFORMATION);

					// Redirect to the login page after logout
					LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
					Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
					primaryStage.setScene(loginScene);
				} catch (Exception e) {
					e.printStackTrace();
					showAlert("Error", "An error occurred during logout.", Alert.AlertType.ERROR);
				}
			}

		});
	}

	// Method to return the create article layout, used in the scene creation
	public GridPane getCreateArticleLayout() {
		return createArticleGrid;
	}

	// Helper method to display alerts to the user
	private void showAlert(String title, String content, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}
