package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> RegisterPage. </p>
 * 
 * <p> Description: This class provides the user interface for creating a new
 * account using an invitation code. It validates user input, checks password
 * strength, and handles user registration with the database. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * <p> This page is accessible to users who are attempting to create a new account. </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 * @version 2.00 2024-10-30 Updated for Phase 2
 * @version 3.00 2024-11-20 Updated for Phase 3
 */
public class RegisterPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The GridPane used to structure the registration page UI. */
    private final GridPane registerGrid;

    /**
     * Constructs the RegisterPage with the given parameters.
     * Initializes the registration page and sets up all components in the 
     * graphical interface, including buttons, labels, and input fields for 
     * registering a new user. It also manages actions for creating an account 
     * and returning to the login page.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     */
    public RegisterPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Setup the registration page layout using GridPane
        registerGrid = new GridPane();
        registerGrid.setAlignment(Pos.CENTER);
        registerGrid.setVgap(10);
        registerGrid.setHgap(10);

        // Define the labels, input fields, and buttons used in the user interface
        Label invitationCodeLabel = new Label("Invitation Code:");
        TextField invitationCodeField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        Button createAccountButton = new Button("Create Account");
        Button backToLoginButton = new Button("Back to Login");

        // Add components to the registration grid layout
        registerGrid.add(invitationCodeLabel, 0, 0);
        registerGrid.add(invitationCodeField, 1, 0);
        registerGrid.add(emailLabel, 0, 1);
        registerGrid.add(emailField, 1, 1);
        registerGrid.add(usernameLabel, 0, 2);
        registerGrid.add(usernameField, 1, 2);
        registerGrid.add(passwordLabel, 0, 3);
        registerGrid.add(passwordField, 1, 3);
        registerGrid.add(confirmPasswordLabel, 0, 4);
        registerGrid.add(confirmPasswordField, 1, 4);
        registerGrid.add(createAccountButton, 1, 5);
        registerGrid.add(backToLoginButton, 1, 6);

        // Adds functionality for the 'Create Account' button
        createAccountButton.setOnAction(event -> handleAccountCreation(invitationCodeField, emailField, usernameField,
                passwordField, confirmPasswordField));

        // Adds functionality for the 'Back to Login' button to redirect to the login page
        backToLoginButton.setOnAction(event -> {
            LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
            Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
            primaryStage.setScene(loginScene);
        });
    }

    /**
     * Handles the process of creating a new account by validating user inputs,
     * checking for existing usernames and emails, and registering the user
     * with the provided invitation code.
     * 
     * @param invitationCodeField the text field for the invitation code
     * @param emailField          the text field for the user's email
     * @param usernameField       the text field for the user's username
     * @param passwordField       the password field for the user's password
     * @param confirmPasswordField the password field for confirming the password
     */
    private void handleAccountCreation(TextField invitationCodeField, TextField emailField, TextField usernameField,
                                       PasswordField passwordField, PasswordField confirmPasswordField) {
        // Collects the entered data from the form
        String invitationCode = invitationCodeField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Validates that all fields are filled and that passwords match
        if (invitationCode.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields must be filled.", Alert.AlertType.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // Use PasswordChecker to validate the strength of the password
        String validationMessage = PasswordChecker.evaluatePassword(password);
        if (!validationMessage.isEmpty()) {
            showAlert("Password Validation Error", validationMessage, Alert.AlertType.ERROR);
            return;
        }

        // Check if the email already exists in the database
        try {
            databaseHelper.ensureConnection();
            boolean emailExists = databaseHelper.emailExists(email);
            if (emailExists) {
                showAlert("Error", "Email Already Exists", Alert.AlertType.ERROR);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if the username already exists in the database
        try {
            databaseHelper.ensureConnection();
            boolean usernameExists = databaseHelper.usernameExists(username);
            if (usernameExists) {
                showAlert("Error", "Username Already Exists", Alert.AlertType.ERROR);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Attempts to register the new user with the provided invitation code
        try {
            databaseHelper.ensureConnection();
            boolean registrationSuccessful = databaseHelper.registerWithInvitationCode(invitationCode, username, password);
            if (registrationSuccessful) {
                databaseHelper.updateUserEmail(username, email);
                showAlert("Success", "Account created successfully. Please log in.", Alert.AlertType.INFORMATION);

                // Redirect to the login page after registration
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                primaryStage.setScene(loginScene);
            } else {
                showAlert("Error", "Invalid invitation code. Please check and try again.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while creating the account.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Returns the registration layout, used in scene creation.
     * 
     * @return the GridPane layout of the Registration Page
     */
    public GridPane getRegisterLayout() {
        return registerGrid;
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