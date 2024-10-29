package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

public class DeleteArticlePage {
	private final Stage primaryStage;
	private final DatabaseHelper databaseHelper;
	private final GridPane deleteArticleGrid;
	
	public DeleteArticlePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
		this.primaryStage = primaryStage;
		this.databaseHelper = databaseHelper;
		
		deleteArticleGrid = new GridPane();
		deleteArticleGrid.setAlignment(Pos.CENTER);
        deleteArticleGrid.setVgap(10);
        deleteArticleGrid.setHgap(10);
        
        Label enterIDLabel = new Label("Enter Article ID: ");
        TextField idTextField = new TextField();
        
        Button deleteArticleButton = new Button("Delete Article");
        Button backButton = new Button("Return to UserHome");
        
        deleteArticleGrid.add(enterIDLabel, 0, 0);
        deleteArticleGrid.add(idTextField, 1, 0);
        deleteArticleGrid.add(backButton, 0, 1);
        deleteArticleGrid.add(deleteArticleButton, 1, 1);
        
        deleteArticleButton.setOnAction(event -> {
       
            String idString = idTextField.getText().trim();
            int id = Integer.parseInt(idString);

            // Checks if the email provided is empty 
            if (idString.isEmpty()) {
                showAlert("Error", "ID must be provided.", Alert.AlertType.ERROR);
                return;
            }
            
            try {
                databaseHelper.deleteArticle(id);
                showAlert("Success", "Article deleted successfully.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while deleting the article.", Alert.AlertType.ERROR);
            }
            
        });
        
        backButton.setOnAction(event -> {
        	try {
        		
        		UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, role);
                Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
                primaryStage.setScene(userHomeScene);
                
        	} catch(Exception e) {
        		 e.printStackTrace();
                 showAlert("Error", "An error occured while returning to home.", Alert.AlertType.ERROR);
        	}
        });
        
        
	}
		public GridPane getDeleteArticleScene() {
				return deleteArticleGrid;
		}
        private void showAlert(String title, String content, Alert.AlertType alertType) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.setHeaderText(null);
            alert.showAndWait();
        }
}
