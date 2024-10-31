package edu.asu.DatabasePart1;

public class ArticleFunctionChecker {
	
	public static String adddingArticle(String title, String difficulty, String authors, String abstractText, String keywords, String body, String references) {
        if (title == null || title.isEmpty()) {
            return "*** Error *** Article title cannot be null or empty.";
        }
        if (difficulty == null || difficulty.isEmpty()) {
            return "*** Error *** Article difficulty cannot be null or empty.";
        }
        if (authors == null || authors.isEmpty()) {
            return "*** Error *** Authors field cannot be null or empty.";
        }
        
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            dbHelper.createArticle(title, difficulty, authors, abstractText, keywords, body, references);
            return "Article successfully added to the database.";
        } catch (Exception e) {
            return "*** Error *** Failed to add article: " + e.getMessage();
        }
	}

        public static String removeArticle(int id) {
            if (id <= 0) {
                return "*** Error *** Article ID must be a positive integer.";
            }
            
            try {
                DatabaseHelper dbHelper = new DatabaseHelper();
                dbHelper.deleteArticle(id);
                return "Article successfully removed from the database.";
            } catch (Exception e) {
                return "*** Error *** Failed to remove article: " + e.getMessage();
            }
        }
}
