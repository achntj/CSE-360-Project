package edu.asu.DatabasePart1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <p> LoginApp. </p>
 * 
 * <p> Description: The main class for the login and registration system. It manages 
 * the launch of the application and determines whether the first-time admin setup or 
 * regular login page should be displayed, depending on the state of the database. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 Login and Registration System
 * @version 2.00 	2024-10-30 Project Phase 2 Login and Registration System
 * 
 */

public class LoginApp extends Application {

    /** Handles database operations such as connection and user queries */
    private final DatabaseHelper databaseHelper = new DatabaseHelper();

    /************
     * The entry point for the JavaFX application.
     * 
     * @param args		The command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /************
     * The start method initializes the primary stage of the application, checks the 
     * state of the database, and directs the user either to the admin setup page or 
     * the login page depending on whether the database is empty.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
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

    /************
     * The stop method is called when the application is closed and ensures that 
     * the database connection is properly closed.
     */
    @Override
    public void stop() {
        // Close the database connection when the application stops
        databaseHelper.closeConnection();
    }
}
