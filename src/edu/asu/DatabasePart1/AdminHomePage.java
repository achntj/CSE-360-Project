package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AdminHomePage {

    private final Stage primaryStage;
    private final DatabaseHelper databaseHelper;
    private final GridPane adminHomeGrid;

    public AdminHomePage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.primaryStage = primaryStage;
        this.databaseHelper = databaseHelper;

        // Setup Admin Home Page Layout
        adminHomeGrid = new GridPane();
        adminHomeGrid.setAlignment(Pos.CENTER);
        adminHomeGrid.setVgap(10);
        adminHomeGrid.setHgap(10);

        Button inviteUserButton = new Button("Invite User");
        Button resetUserAccountButton = new Button("Reset User Account");
        Button deleteUserAccountButton = new Button("Delete User Account");
        Button listUserAccountsButton = new Button("List User Accounts");
        Button addRemoveRoleButton = new Button("Add/Remove User Role");
        Button logoutButton = new Button("Log Out");

        // Add buttons to the layout
        adminHomeGrid.add(inviteUserButton, 0, 0);
        adminHomeGrid.add(resetUserAccountButton, 0, 1);
        adminHomeGrid.add(deleteUserAccountButton, 0, 2);
        adminHomeGrid.add(listUserAccountsButton, 0, 3);
        adminHomeGrid.add(addRemoveRoleButton, 0, 4);
        adminHomeGrid.add(logoutButton, 0, 5);

        // Invite User Button Action
        inviteUserButton.setOnAction(event -> {
            InviteUserPage inviteUserPage = new InviteUserPage(primaryStage, databaseHelper);
            Scene inviteUserScene = new Scene(inviteUserPage.getInviteUserLayout(), 400, 300);
            primaryStage.setScene(inviteUserScene);
        });

        // Reset User Account Button Action
        resetUserAccountButton.setOnAction(event -> {
            ResetUserAccountPage resetUserPage = new ResetUserAccountPage(primaryStage, databaseHelper);
            Scene resetUserScene = new Scene(resetUserPage.getResetUserLayout(), 400, 300);
            primaryStage.setScene(resetUserScene);
        });

        // Delete User Account Button Action
        deleteUserAccountButton.setOnAction(event -> {
            DeleteUserAccountPage deleteUserPage = new DeleteUserAccountPage(primaryStage, databaseHelper);
            Scene deleteUserScene = new Scene(deleteUserPage.getDeleteUserLayout(), 400, 300);
            primaryStage.setScene(deleteUserScene);
        });

        // List User Accounts Button Action
        listUserAccountsButton.setOnAction(event -> {
            try {
                // Display list of users (could be updated to a new page as needed)
                String usersList = databaseHelper.listUserAccounts();
                showAlert("User Accounts", usersList, Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while listing user accounts.", Alert.AlertType.ERROR);
            }
        });

        // Add/Remove Role Button Action
        addRemoveRoleButton.setOnAction(event -> {
            AddRemoveRolePage addRemoveRolePage = new AddRemoveRolePage(primaryStage, databaseHelper);
            Scene addRemoveRoleScene = new Scene(addRemoveRolePage.getAddRemoveRoleLayout(), 400, 300);
            primaryStage.setScene(addRemoveRoleScene);
        });

        // Log Out Button Action
        logoutButton.setOnAction(event -> {
            // Redirect to Login Page
            LoginPage loginPage = new LoginPage(primaryStage, databaseHelper);
            Scene loginScene = new Scene(loginPage.getLoginLayout(), 400, 300);
            primaryStage.setScene(loginScene);
        });
    }

    public GridPane getAdminHomeLayout() {
        return adminHomeGrid;
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
