package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> LoginPage. </p>
 * 
 * <p> Description: This class provides the user interface for the login page,
 * allowing users to enter their email and password to log in. It handles
 * validation of user credentials, manages redirection based on user roles
 * and account status, and provides access to the registration page. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * <p> This page is accessible to all users for authentication. </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 */
public class LoginPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The GridPane used to structure the login page UI. */
    private final GridPane loginGrid;

    /**
     * Constructs the LoginPage with the given parameters.
     * Initializes the login page and sets up all components in the graphical
     * interface, including buttons, labels, and text fields. It also manages
     * actions for logging in and navigating to the registration page.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     */
    public LoginPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Setup the login page layout using GridPane
        loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setVgap(10);
        loginGrid.setHgap(10);

        // Define the labels, text fields, and buttons used in the user interface
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        // Add components to the login grid layout
        loginGrid.add(emailLabel, 0, 0);
        loginGrid.add(emailField, 1, 0);
        loginGrid.add(passwordLabel, 0, 1);
        loginGrid.add(passwordField, 1, 1);
        loginGrid.add(loginButton, 1, 2);
        loginGrid.add(registerButton, 1, 3);

        // Adds functionality for the 'Login' button
        loginButton.setOnAction(event -> handleLogin(emailField, passwordField));

        // Adds functionality for the 'Register' button to redirect to the registration page
        registerButton.setOnAction(event -> {
            RegisterPage registerPage = new RegisterPage(primaryStage, databaseHelper);
            Scene registerScene = new Scene(registerPage.getRegisterLayout(), 400, 400);
            primaryStage.setScene(registerScene);
        });
    }

    /**
     * Handles the login process, including validation of input fields,
     * authentication of user credentials, and redirection based on account
     * status and user roles.
     * 
     * @param emailField    the text field where the user enters their email
     * @param passwordField the password field where the user enters their password
     */
    private void handleLogin(TextField emailField, PasswordField passwordField) {
        // Collects the entered email and password
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validates that both fields are filled, shows an error if not
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and Password fields cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Ensures that a connection to the database is established
            databaseHelper.ensureConnection();

            // Verifies the login credentials
            if (databaseHelper.login(email, password)) {
                if (databaseHelper.isOneTimePassword(email)) {
                    // Redirects to the password reset page
                    SetNewPassword passwordResetPage = new SetNewPassword(primaryStage, databaseHelper, email);
                    Scene resetScene = new Scene(passwordResetPage.getPasswordResetLayout(), 400, 300);
                    primaryStage.setScene(resetScene);

                } else if (!databaseHelper.isAccountSetupComplete(email)) {
                    // Redirects to the account setup page
                    CompleteAccountSetupPage setupPage = new CompleteAccountSetupPage(primaryStage, databaseHelper,
                            email);
                    Scene setupScene = new Scene(setupPage.getCompleteSetupLayout(), 400, 300);
                    primaryStage.setScene(setupScene);

                } else {
                    // Check if the user has multiple roles
                    if (databaseHelper.hasMultipleRoles(email)) {
                        RoleSelectionPage roleSelectionPage = new RoleSelectionPage(primaryStage, databaseHelper,
                                email);
                        Scene roleScene = new Scene(roleSelectionPage.getRoleSelectionLayout(), 400, 300);
                        primaryStage.setScene(roleScene);
                    }
                    // Redirect to the user home page based on the user's role
                    else if (databaseHelper.hasRole(email, "admin")) {
                        AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
                        Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
                        primaryStage.setScene(adminScene);
                    } else if (databaseHelper.hasRole(email, "instructor")) {
                        InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper,
                                email, "instructor");
                        Scene instructorScene = new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300);
                        primaryStage.setScene(instructorScene);
                    } else if (databaseHelper.hasRole(email, "student")) {
                        StudentHomePage studentHomePage = new StudentHomePage(primaryStage, databaseHelper, email,
                                "student");
                        Scene studentScene = new Scene(studentHomePage.getStudentHomeLayout(), 400, 300);
                        primaryStage.setScene(studentScene);
                    } else {
                        String role = databaseHelper.getUserRole(email);
                        UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, role);
                        Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
                        primaryStage.setScene(userHomeScene);
                    }
                }
            } else {
                showAlert("Login Failed", "Invalid email or password. Please try again.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while accessing the database.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Returns the login layout, used in scene creation.
     * 
     * @return the GridPane layout of the Login Page
     */
    public GridPane getLoginLayout() {
        return loginGrid;
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