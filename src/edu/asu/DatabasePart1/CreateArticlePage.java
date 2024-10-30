package edu.asu.DatabasePart1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CreateArticlePage {
	
	private final Stage primaryStage;
	private final DatabaseHelper databaseHelper;
	private final String email;
	private final GridPane createArticleGrid;
	
	public CreateArticlePage(Stage primaryStage, DatabaseHelper databaseHelper, String email, String role) {
		this.primaryStage = primaryStage;
		this.databaseHelper = databaseHelper;
		this.email = email;
		
		createArticleGrid = new GridPane();
		createArticleGrid.setAlignment(Pos.CENTER);
		createArticleGrid.setVgap(10);
		createArticleGrid.setHgap(10);
		
		Label titleLabel = new Label("Title: ");
		TextField titleTextField = new TextField();
		
		Label difficultyLabel = new Label("Difficulty Level:");
		ComboBox<String> difficultyComboBox = new ComboBox<>();
		difficultyComboBox.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
		difficultyComboBox.setValue("Beginner");  // Set default value
		
		Label authorsLabel = new Label("Authors:");
	    TextField authorsTextField = new TextField();
	    
	    Label abstractLabel = new Label("Abstract:");
	    TextField abstractTextField = new TextField();

        Label keywordsLabel = new Label("Keywords:");
        TextField keywordsField = new TextField();

        Label bodyLabel = new Label("Body:");
        TextField bodyField = new TextField();
		
        Label referenceLinksLabel = new Label("References:");
        TextField referenceLinksField = new TextField();
        
        Button createArticleButton = new Button("Complete Article Setup");
        Button backButton = new Button("Return to Homepage");
		
        createArticleGrid.add(titleLabel, 0, 0);
        createArticleGrid.add(titleTextField, 1, 0);
        
        createArticleGrid.add(difficultyLabel, 0, 1);
        createArticleGrid.add(difficultyComboBox, 1, 1);
        
        createArticleGrid.add(authorsLabel, 0, 2);
        createArticleGrid.add(authorsTextField, 1, 2);

        createArticleGrid.add(abstractLabel, 0, 3);
        createArticleGrid.add(abstractTextField, 1, 3);

        createArticleGrid.add(keywordsLabel, 0, 4);
        createArticleGrid.add(keywordsField, 1, 4);

        createArticleGrid.add(bodyLabel, 0, 5);
        createArticleGrid.add(bodyField, 1, 5);

        createArticleGrid.add(referenceLinksLabel, 0, 6);
        createArticleGrid.add(referenceLinksField, 1, 6);

        createArticleGrid.add(backButton, 0, 7);
        createArticleGrid.add(createArticleButton, 1, 7);

        createArticleButton.setOnAction(event -> {
        	String title = titleTextField.getText().trim();
        	String difficulty = difficultyComboBox.getValue();
        	String authors = authorsTextField.getText().trim();
        	String abstractVal = abstractTextField.getText().trim();
        	String keywords = keywordsField.getText().trim();
        	String body = bodyField.getText().trim();
        	String references = referenceLinksField.getText().trim();
        	
        	if (title.isEmpty() || authors.isEmpty() || abstractVal.isEmpty() || keywords.isEmpty() || body.isEmpty() || references.isEmpty()) {
        		showAlert("Error", "Empty text fields, please fill out all categories!", Alert.AlertType.ERROR);
        		return;
        	}
        	
        	try {
        		databaseHelper.createArticle(title, difficulty, authors, abstractVal, keywords, body, references);
        		showAlert("Success", "Article Added Successfully!", Alert.AlertType.INFORMATION);
        		
        		UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, role);
                Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
                primaryStage.setScene(userHomeScene);
        		
        	} catch (Exception e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while adding the article.", Alert.AlertType.ERROR);
            }
        	
        });
        
        backButton.setOnAction(event -> {
        	try {
        		UserHomePage userHomePage = new UserHomePage(primaryStage, databaseHelper, email, role);
                Scene userHomeScene = new Scene(userHomePage.getUserHomeLayout(), 400, 300);
                primaryStage.setScene(userHomeScene);
        	} catch(Exception e) {
        		 e.printStackTrace();
                 showAlert("Error", "An error occurred while returning to home.", Alert.AlertType.ERROR);
        	}
        });
	}
	
	public GridPane getCreateArticleLayout() {
        return createArticleGrid;
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
