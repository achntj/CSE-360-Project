package edu.asu.DatabasePart1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginApp extends Application {

    private final DatabaseHelper databaseHelper = new DatabaseHelper();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Connect to the database
            databaseHelper.connectToDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        primaryStage.setTitle("Login and Registration System");

        // Check if the database is empty; if so, redirect to Admin registration page
        try {
            if (databaseHelper.isDatabaseEmpty()) {
                // This is the first user, they need to create an Admin account
                AdminSetupPage adminSetupPage = new AdminSetupPage(primaryStage, databaseHelper);
                Scene adminSetupScene = new Scene(adminSetupPage.getAdminSetupLayout(), 400, 300);
                primaryStage.setScene(adminSetupScene);
            } else {
                // Instantiate the LoginPage and set the scene for regular users
                LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
                Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
                primaryStage.setScene(loginScene);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        primaryStage.show();
    }

    @Override
    public void stop() {
        databaseHelper.closeConnection();
    }
}

