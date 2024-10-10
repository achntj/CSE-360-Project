package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> RoleSelectionPage. </p>
 * 
 * <p> Description: This class provides the user interface for selecting a role when a 
 * user has multiple roles. The selected role will determine which home page the user is 
 * redirected to for the current session. </p>
 * 
 * @version 1.00 	2024-10-09 Project Phase 1 Role Selection Page
 * 
 */

public class RoleSelectionPage {

    /** The primary stage used for the GUI interface */
    private final Stage primaryStage;
    
    /** The database helper that allows interactions with the user database */
    private final DatabaseHelper databaseHelper;
    
    /** The email of the user who is selecting a role */
    private final String email;
    
    /** The Grid Pane used to structure the role selection page UI */
    private final GridPane roleSelectionGrid;

    /************
     * This constructor initializes the role selection page and sets up all of the 
     * components in the graphical interface, including radio buttons for each role 
     * the user holds, and a button to confirm the selection. It also manages the 
     * redirection to the corresponding home page based on the selected role.
     * 
     * @param primaryStage		The primary stage used to display the graphical interface
     * @param databaseHelper	The database helper that enables interaction with the database
     * @param email				The email of the user who is selecting a role
     */
    public RoleSelectionPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
    	
    	// Initializes the primaryStage, database helper, and email
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup the layout for the role selection page using GridPane
        roleSelectionGrid = new GridPane();
        roleSelectionGrid.setAlignment(Pos.CENTER);
        roleSelectionGrid.setVgap(10);
        roleSelectionGrid.setHgap(10);

        // Define the label and toggle group for selecting roles
        Label selectRoleLabel = new Label("Select Role for This Session:");
        ToggleGroup roleGroup = new ToggleGroup();

        // Retrieve and display the user's available roles
        try {
            String[] roles = databaseHelper.getUserRoles(email);
            int row = 1;

            // Add radio buttons for each role in the grid
            for (String role : roles) {
                RadioButton roleOption = new RadioButton(role);
                roleOption.setToggleGroup(roleGroup);
                roleSelectionGrid.add(roleOption, 0, row++);
            }

            // Add the label and button for role selection
            Button selectRoleButton = new Button("Select Role");
            roleSelectionGrid.add(selectRoleLabel, 0, 0);
            roleSelectionGrid.add(selectRoleButton, 0, row);

            // Adds functionality for the 'Select Role' button
            selectRoleButton.setOnAction(event -> {
                // Retrieve the selected radio button
                RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();
                if (selectedRole == null) {
                    showAlert("Error", "Please select a role.", Alert.AlertType.ERROR);
                    return;
                }

                // Retrieve the value of the selected role
                String selectedRoleValue = selectedRole.getText();

                // Redirect to the user home page based on the selected role
                try {
                    UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, selectedRoleValue);
                    Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
                    primaryStage.setScene(userHomeScene);
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "An error occurred while loading the user home page.", Alert.AlertType.ERROR);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while retrieving user roles.", Alert.AlertType.ERROR);
        }
    }

    // Method to return the role selection layout, used in the scene creation
    public GridPane getRoleSelectionLayout() {
        return roleSelectionGrid;
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
