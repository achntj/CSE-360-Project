package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * <p> RoleSelectionPage. </p>
 * 
 * <p> Description: This class provides the user interface for selecting a role 
 * when a user has multiple roles. The selected role determines which home page 
 * the user is redirected to for the current session. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha,
 *         Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Initial Version
 * @version 2.00 2024-10-30 Updated for Phase 2
 * @version 3.00 2024-11-20 Updated for Phase 3
 */
public class RoleSelectionPage {

    /** The primary stage used for the GUI interface. */
    private final Stage primaryStage;

    /** The database helper that allows interactions with the user database. */
    private final DatabaseHelper databaseHelper;

    /** The email of the user who is selecting a role. */
    private final String email;

    /** The GridPane used to structure the role selection page UI. */
    private final GridPane roleSelectionGrid;

    /**
     * Constructs the RoleSelectionPage with the given parameters.
     * Initializes the role selection page and sets up all components in the 
     * graphical interface, including radio buttons for each role the user holds, 
     * and a button to confirm the selection. It also manages the redirection to 
     * the corresponding home page based on the selected role.
     * 
     * @param primaryStage   the primary stage used to display the graphical interface
     * @param databaseHelper the database helper that enables interaction with the database
     * @param email          the email of the user who is selecting a role
     */
    public RoleSelectionPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
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
                String selectedRoleValue = selectedRole.getText().trim();

                // Redirect to the user home page based on the selected role
                try {
                    if (selectedRoleValue.equalsIgnoreCase("admin")) {
                        AdminHomePage adminHomePage = new AdminHomePage(primaryStage, databaseHelper, email);
                        Scene adminScene = new Scene(adminHomePage.getAdminHomeLayout(), 400, 300);
                        primaryStage.setScene(adminScene);
                    } else if (selectedRoleValue.equalsIgnoreCase("instructor")) {
                        InstructorHomePage instructorHomePage = new InstructorHomePage(primaryStage, databaseHelper,
                                email, "instructor");
                        Scene instructorScene = new Scene(instructorHomePage.getInstructorHomeLayout(), 400, 300);
                        primaryStage.setScene(instructorScene);
                    } else if (selectedRoleValue.equalsIgnoreCase("student")) {
                        SearchPage searchPage = new SearchPage(primaryStage, databaseHelper, email, "student");
                        Scene studentScene = new Scene(searchPage.getSearchLayout(), 400, 300);
                        primaryStage.setScene(studentScene);
                    } else {
                        // Default redirection for unhandled roles
                        String role = databaseHelper.getUserRole(email);
                        UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, role);
                        Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
                        primaryStage.setScene(userHomeScene);
                    }
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

    /**
     * Returns the role selection layout, used in scene creation.
     * 
     * @return the GridPane layout of the Role Selection Page
     */
    public GridPane getRoleSelectionLayout() {
        return roleSelectionGrid;
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