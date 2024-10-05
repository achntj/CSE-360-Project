module CSE_360_MAIN_PROJECT {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Add this for H2 database usage
    
    exports edu.asu.DatabasePart1 to javafx.graphics;
}

