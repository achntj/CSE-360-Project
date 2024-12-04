package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p>
 * InviteUserPage.
 * </p>
 * 
 * <p>
 * Description: Setup for an interactive JavaFX page that allows an admin user
 * to invite new users to the system by generating invitation codes tied to
 * specific roles.
 * </p>
 * 
 * <p>
 * Copyright: Group 11 - CSE 360 © 2024
 * </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya
 *         Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Project Phase 1 Invite User Page
 * 
 */

public class DeleteStudentPage {

	private final Stage primaryStage;

	/**
	 * Allows us to update and edit the database that holds user information and
	 * generate invitation codes
	 */
	private final DatabaseHelper databaseHelper;

	/** The email of the logged-in user */
	private final String email;

	/** The role of the logged-in user */
	private final String role;

	/** The Grid Pane used to structure the invite user page */
	private final GridPane inviteUserGrid;

	/************
	 * This method initializes all of the elements used in the graphical interface
	 * presented for the InviteUserPage. It sets up the alignment, text fields, and
	 * buttons used for the invitation functionality and provides error handling.
	 * 
	 * @param primaryStage   Input of primaryStage used to manage the graphical
	 *                       interface changes
	 * @param databaseHelper Input of the databaseHelper that enables interaction
	 *                       with the database
	 */
	public DeleteStudentPage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {

		// Initializes the primaryStage and database helper
		this.primaryStage = primaryStage;
		this.databaseHelper = databaseHelper;
		this.email = email;
		this.role = role;

		// Creates a new GridPane and sets its alignment and spacing
		inviteUserGrid = new GridPane();
		inviteUserGrid.setAlignment(Pos.CENTER);
		inviteUserGrid.setVgap(10);
		inviteUserGrid.setHgap(10);

		// Establishes text fields and buttons used in the user interface
		Label studentIDField = new Label("Student ID: ");
		TextField idField = new TextField();
		Button deleteButton = new Button("Delete Student");
		Button backButton = new Button("Back");

		// Adds the fields and buttons to the interface
		inviteUserGrid.add(studentIDField, 0, 0);
		inviteUserGrid.add(idField, 2, 0);

		inviteUserGrid.add(deleteButton, 2, 2);
		inviteUserGrid.add(backButton, 0, 2);

		// Adds functionality for the 'Generate Invitation Code' button
		deleteButton.setOnAction(event -> {
			// Retrieves the entered role(s) to associate with the invitation code
			String studentIDString = idField.getText().trim();
			
			
			if (studentIDString.isBlank()) {
				showAlert("Error", "ID must be specified.", Alert.AlertType.ERROR);
				return;
			}
			
			int studentID = Integer.parseInt(studentIDString);
			
			
			Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
	                "Are you sure you want to delete this user?", 
	                ButtonType.YES, ButtonType.NO);
	        confirmation.setTitle("Confirm Deletion");
	        confirmation.setHeaderText(null);
	        confirmation.showAndWait();

	        if (confirmation.getResult() == ButtonType.YES) {
	            try {
	            	String studentEmail = databaseHelper.getEmail(studentID);
	                databaseHelper.deleteUserAccount(studentEmail);
	                showAlert("Success", "Student deleted successfully.", Alert.AlertType.INFORMATION);
	            } catch (SQLException e) {
	                e.printStackTrace();
	                showAlert("Database Error", "An error occurred while deleting the user account.", Alert.AlertType.ERROR);
	            }
	        }
			
		});

		// Adds functionality for the 'Back' button to return to the instructor home
		// page
		backButton.setOnAction(event -> {
			// Navigates back to the Instructor Home Page
			InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper, email, role);
			Scene instructorScene = new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300);
			primaryStage.setScene(instructorScene);
		});
	}

	// Get function for inviteUserGrid layout
	public GridPane getDeleteStudentLayout() {
		return inviteUserGrid;
	}

	// Creates and displays pop up alert messages to the user
	private void showAlert(String title, String content, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setContentText(content);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}
