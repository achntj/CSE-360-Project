package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * <p> UserHomePage. </p>
 * 
 * <p> Description: This class provides the user interface for the home page that users 
 * are redirected to after login. The layout is role-specific, and users can log out 
 * from this page. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 User Home Page
 * 
 */

public class UserHomePage {

    /** The primary stage used for the GUI interface */
    private final Stage primaryStage;
    
    /** The database helper that allows interactions with the user database */
    private final DatabaseHelper databaseHelper;
    
    /** The email of the logged-in user */
    private final String email;
    
    /** The role of the logged-in user */
    private final String role;
    
    /** The Grid Pane used to structure the user home page UI */
    private final GridPane homeGrid;

    /************
     * This constructor initializes the user home page and sets up all of the 
     * components in the graphical interface, including a welcome message and a 
     * logout button. It also handles the logout action and redirection to the 
     * login page.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
     * @param databaseHelper	The database helper that enables interaction with the database
     * @param email				The email of the logged-in user
     * @param role				The role of the logged-in user
     */
    public UserHomePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
    	
    	// Initializes the primaryStage, database helper, email, and role
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;
        this.role = role;

        // Setup the layout for the user home page using GridPane
        homeGrid = new GridPane();
        homeGrid.setAlignment(Pos.CENTER);
        homeGrid.setVgap(10);
        homeGrid.setHgap(10);

        // Define the welcome label and logout button
        Label welcomeLabel = new Label("Welcome to User Home Page, " + role + "!");
        
        Button createArticleButton = new Button("Create Article");
        Button listArticlesButton = new Button("List Articles");
        Button deleteArticleButton = new Button("Delete Article");
        Button backupArticlesButton = new Button("Backup Articles");
        Button restoreArticlesButton = new Button("Restore Articles");
        Button logoutButton = new Button("Log Out");
        
        /*
        // Main menu for user commands
        while (true) {
            System.out.println("Choose an option: ");
            System.out.println("1. Create Article");
            System.out.println("2. List Articles");
            System.out.println("3. Delete Article");
            System.out.println("4. Backup Articles");
            System.out.println("5. Restore Articles");
            System.out.println("0. Exit");

            
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    createArticle();
                    break;
                case "2":
                    listArticles();
                    break;
                case "3":
                    deleteArticle();
                    break;
                case "4":
                    backupArticles();
                    break;
                case "5":
                    restoreArticles();
                    break;
                case "0":
                    System.out.println("Good Bye!!");
                    databaseHelper.closeConnection();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        */

        // Add components to the home grid layout
        homeGrid.add(welcomeLabel, 0, 0);
        homeGrid.add(createArticleButton, 0, 1);
        homeGrid.add(listArticlesButton, 0, 2);
        homeGrid.add(deleteArticleButton, 0, 3);
        homeGrid.add(backupArticlesButton, 0, 4);
        homeGrid.add(restoreArticlesButton, 0, 5); 
        homeGrid.add(logoutButton, 0, 6);

        createArticleButton.setOnAction(evenet -> {
        	try {
        		databaseHelper.ensureConnection();
        		
        		//redirect to create article page
        		CreateArticlePage createArticlePage = new CreateArticlePage(primaryStage, databaseHelper, email, role);
                Scene createArticleScene = new Scene(createArticlePage.getCreateArticleLayout(), 400, 300);
                primaryStage.setScene(createArticleScene);
        	} catch (Exception e) {
        		e.printStackTrace();
                showAlert("Error", "An error occurred while entering create and article.", Alert.AlertType.ERROR);
        	}
        });
        
        listArticlesButton.setOnAction(event -> {
        	try {
                // Gathers the users accounts from the database and attempts to display them
                String articleList = databaseHelper.listArticles();
                showAlert("Articles", articleList, Alert.AlertType.INFORMATION); 
                // Checks if there was an error in displaying the user accounts and alerts the user
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while listing articles.", Alert.AlertType.ERROR);
            }
        	
        });
        
        deleteArticleButton.setOnAction(event -> {
        	
        	try {
        		databaseHelper.ensureConnection();
        		
        		//redirect to create article page
        		DeleteArticlePage deleteArticlePage = new DeleteArticlePage(primaryStage, databaseHelper, email, role);
                Scene deleteArticleScene = new Scene(deleteArticlePage.getDeleteArticleScene(), 400, 300);
                primaryStage.setScene(deleteArticleScene);
        	} catch (Exception e) {
        		e.printStackTrace();
                showAlert("Error", "An error occurred while entering create and article.", Alert.AlertType.ERROR);
        	}
        });
        
        backupArticlesButton.setOnAction(event -> {
        	showAlert("Info", "Backing up Articles...", Alert.AlertType.INFORMATION );
        	try {
        		databaseHelper.ensureConnection();
        		databaseHelper.backupArticles("articleBackup.txt");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
                showAlert("Error", "An error occurred while backing up articles.", Alert.AlertType.ERROR);
        	}
        });
        
        restoreArticlesButton.setOnAction(event -> {
        	showAlert("Info", "Restoring Articles...", Alert.AlertType.INFORMATION );
        	try {
        		databaseHelper.ensureConnection();
        		databaseHelper.restoreArticles("articleBackup.txt");
        		
        	} catch (Exception e) {
        		e.printStackTrace();
                showAlert("Error", "An error occurred while backing up articles.", Alert.AlertType.ERROR);
        	}
        });
               
        
        // Adds functionality for the 'Log Out' button
        logoutButton.setOnAction(event -> {
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
        });
                
    }

    // Method to return the user home layout, used in the scene creation
    public GridPane getUserHomeLayout() {
        return homeGrid;
    }

    // Helper method to display alerts to the user
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    
    /**
     * Prompts the user to enter details for a new article and creates it in the database.
     *
     * @throws Exception if an error occurs during article creation
     */
    /*
    private static void createArticle() throws Exception {
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Authors (comma-separated): ");
        String authors = scanner.nextLine();
        System.out.print("Enter Abstract: ");
        String abstractText = scanner.nextLine();
        System.out.print("Enter Keywords (comma-separated): ");
        String keywords = scanner.nextLine();
        System.out.print("Enter Body: ");
        String body = scanner.nextLine();
        System.out.print("Enter References (comma-separated): ");
        String references = scanner.nextLine();

        databaseHelper.createArticle(title, authors, abstractText, keywords, body, references);
    }
    

    /**
     * Displays a list of articles from the database.
     *
     * @throws Exception if an error occurs during article retrieval
     */
    
    /*
    private static void listArticles() throws Exception {
        databaseHelper.displayArticles();
    }

    /**
     * Prompts the user for an article ID and deletes the corresponding article from the database.
     *
     * @throws Exception if an error occurs during article deletion
     */
    
    /*
    private static void deleteArticle() throws Exception {
        System.out.print("Enter Article ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());
        databaseHelper.deleteArticle(id);
    }

    /**
     * Prompts the user for a backup file name and backs up the articles to the specified file.
     *
     * @throws Exception if an error occurs during the backup process
     */
    
    /*
    private static void backupArticles() throws Exception {
        System.out.print("Enter backup file name: ");
        String fileName = scanner.nextLine();
        databaseHelper.backupArticles(fileName);
    }

    /**
     * Prompts the user for a restore file name and restores articles from the specified file.
     *
     * @throws Exception if an error occurs during the restore process
     */
    
    /*
    private static void restoreArticles() throws Exception {
        System.out.print("Enter restore file name: ");
        String fileName = scanner.nextLine();
        databaseHelper.restoreArticles(fileName);
    }
    */
}
