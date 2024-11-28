package edu.asu.DatabasePart1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <p> LoginApp. </p>
 * 
 * <p> Description: The main class for the login and registration system. It 
 * manages the launch of the application and determines whether the first-time 
 * admin setup or regular login page should be displayed, depending on the state 
 * of the database. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * <p> This class serves as the entry point for the JavaFX application. </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 * @version 2.00 2024-10-30 Updated for Phase 2
 */
public class LoginApp extends Application {

    /** Handles database operations such as connection and user queries. */
    private final DatabaseHelper databaseHelper = new DatabaseHelper();

    /**
     * The main entry point for the application.
     * Launches the JavaFX application and initializes the primary stage.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the primary stage of the application.
     * Determines the initial page to display (Admin Setup Page or Login Page) 
     * based on the state of the database. If the database is empty, it redirects 
     * to the Admin Setup Page for first-time setup. Otherwise, it loads the 
     * regular login page.
     * 
     * @param primaryStage the primary stage used to display the graphical interface
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Connect to the database
            databaseHelper.connectToDatabase();
        } catch (Exception e) {
            // If an error occurs while connecting, print the stack trace and exit the application
            e.printStackTrace();
            System.exit(1);
        }

        // Set the title of the primary stage
        primaryStage.setTitle("Login and Registration System");

        // Check if the database is empty to determine the initial page
        try {
            if (databaseHelper.isDatabaseEmpty()) {
                // If the database is empty, redirect to Admin Setup Page for first-time setup
                AdminSetupPage adminSetupPage = new AdminSetupPage(primaryStage, databaseHelper);
                Scene adminSetupScene = new Scene(adminSetupPage.getAdminSetupLayout(), 400, 300);
                primaryStage.setScene(adminSetupScene);
            } else {
                // Otherwise, show the regular login page for existing users
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                primaryStage.setScene(loginScene);
            }
        } catch (Exception e) {
            // If an error occurs, print the stack trace
            e.printStackTrace();
        }

        // Display the primary stage
        primaryStage.show();
    }

    /**
     * Ensures proper cleanup when the application stops.
     * This method is called automatically by the JavaFX framework when the
     * application is closed. It ensures that the database connection is properly
     * closed to prevent resource leaks.
     */
    @Override
    public void stop() {
        databaseHelper.closeConnection();
    }
}