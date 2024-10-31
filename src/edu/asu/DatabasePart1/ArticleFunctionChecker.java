package edu.asu.DatabasePart1;

public class ArticleFunctionChecker {
	
	public static boolean articleAdded;
	public static boolean articleDeleted;
	
	public static DatabaseHelper databaseHelper;
	
	
	public static String addedAndDeleted(String title, String difficulty, String authors, String abstractText, String keywords, String body, String references) {
		boolean articleAdded = false;
		boolean articleDeleted = false;
		
		databaseHelper = new DatabaseHelper();
		
		int id;
		
		if (title == null || title.isEmpty()) {
            return "*** Error *** Article title cannot be null or empty.";
        }
        if (difficulty == null || difficulty.isEmpty()) {
            return "*** Error *** Article difficulty cannot be null or empty.";
        }
        if (authors == null || authors.isEmpty()) {
            return "*** Error *** Authors field cannot be null or empty.";
        }
        if (title == null || abstractText.isEmpty()) {
            return "*** Error *** Abstract cannot be null or empty.";
        }
        if (difficulty == null || keywords.isEmpty()) {
            return "*** Error *** Keywords cannot be null or empty.";
        }
        if (authors == null || body.isEmpty()) {
            return "*** Error *** Body cannot be null or empty.";
        }
        if (authors == null || references.isEmpty()) {
            return "*** Error *** References cannot be null or empty.";
        }    
        
        //Creating and article
        try {
        	id = databaseHelper.getArticleID(title);
            databaseHelper.createArticle(title, difficulty, authors, abstractText, keywords, body, references);
            
            articleAdded = true;
            
            
        } catch (Exception e) {
            return "*** Error *** Failed to add article: " + e.getMessage();
        }
        
        try {
        	id = databaseHelper.getArticleID(title);
    
        } catch (Exception e) {
        	return "*** Error *** Failed to get ID:  e.getMessage()";
        } 
        
        try {
        	System.out.println(databaseHelper.listArticles());
            
        } catch (Exception e) {
            return "*** Error *** Failed to list articles: " + e.getMessage();
        }
        
        try {
        	
            databaseHelper.deleteArticle(id);
           
            articleDeleted = true;
            
        } catch (Exception e) {
            return "*** Error *** Failed to delete article: " + e.getMessage();
        }
        
        try {
        	System.out.println(databaseHelper.listArticles());
        } catch (Exception e) {
            return "*** Error *** Failed to list articles: " + e.getMessage();
        }
        
        return "";
        
        
        
	}
	
}
