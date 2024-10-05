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

public class RoleSelectionPage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final String email;
    private final GridPane roleSelectionGrid;

    public RoleSelectionPage(Stage primaryStage, DatabaseHelper databaseHelper, String email) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;
        this.email = email;

        // Setup Role Selection UI Layout
        roleSelectionGrid = new GridPane();
        roleSelectionGrid.setAlignment(Pos.CENTER);
        roleSelectionGrid.setVgap(10);
        roleSelectionGrid.setHgap(10);

        Label selectRoleLabel = new Label("Select Role for This Session:");
        ToggleGroup roleGroup = new ToggleGroup();

        try {
            String[] roles = databaseHelper.getUserRoles(email);
            int row = 1;

            // Add radio buttons for each role
            for (String role : roles) {
                RadioButton roleOption = new RadioButton(role);
                roleOption.setToggleGroup(roleGroup);
                roleSelectionGrid.add(roleOption, 0, row++);
            }

            Button selectRoleButton = new Button("Select Role");
            roleSelectionGrid.add(selectRoleLabel, 0, 0);
            roleSelectionGrid.add(selectRoleButton, 0, row);

            // Select Role Button Action
            selectRoleButton.setOnAction(event -> {
                RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();
                if (selectedRole == null) {
                    showAlert("Error", "Please select a role.", Alert.AlertType.ERROR);
                    return;
                }

                // Retrieve the selected role
                String selectedRoleValue = selectedRole.getText();

                try {
                    // Redirect to user home page based on the selected role
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

    public GridPane getRoleSelectionLayout() {
        return roleSelectionGrid;
    }

    // Helper method to show alerts to the user
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
