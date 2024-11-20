package edu.asu.DatabasePart1;

/**
 * ArticleFunctionChecker Class
 * 
 * <p>
 * Description: This class provides methods to validate, add, and delete articles
 * in the database using the DatabaseHelper class. It performs checks on the input
 * data, adds an article to the database, retrieves its ID, lists articles, and deletes
 * the added article.
 * </p>
 * 
 * <p>
 * Copyright: Group 11 - CSE 360 © 2024
 * </p>
 * 
 * @author
 * 
 * @version 1.00 2024-10-09 Initial creation and testing of methods for article
 *          addition and deletion.
 */
public class ArticleFunctionChecker {

    // Flags to indicate if an article was added and deleted successfully
    public static boolean articleAdded;
    public static boolean articleDeleted;

    // Database helper instance to perform database operations
    public static DatabaseHelper databaseHelper;

    /**
     * Validates, adds, and deletes an article in the database.
     * 
     * <p>
     * The method performs validation checks on article fields (title, difficulty,
     * authors, abstract, keywords, body, references) before attempting to add the
     * article to the database. After adding, it retrieves the article ID, lists
     * the articles, and deletes the added article, updating status flags for each
     * action.
     * </p>
     * 
     * @param title        Title of the article.
     * @param difficulty   Difficulty level of the article.
     * @param authors      Authors of the article.
     * @param abstractText Abstract description of the article.
     * @param keywords     Keywords associated with the article.
     * @param body         Main content of the article.
     * @param references   References cited in the article.
     * @return Error message if any validation or database operation fails; empty
     *         string if successful.
     */
    public static String addedAndDeleted(String title, String difficulty, String authors, String abstractText,
            String keywords, String body, String references) {
        // Initialize status flags and helper instance
        boolean articleAdded = false;
        boolean articleDeleted = false;
        databaseHelper = new DatabaseHelper();

        int id;

        // Input validation checks for article fields
        if (title == null || title.isEmpty()) {
            return "*** Error *** Article title cannot be null or empty.";
        }
        if (difficulty == null || difficulty.isEmpty()) {
            return "*** Error *** Article difficulty cannot be null or empty.";
        }
        if (authors == null || authors.isEmpty()) {
            return "*** Error *** Authors field cannot be null or empty.";
        }
        if (abstractText == null || abstractText.isEmpty()) {
            return "*** Error *** Abstract cannot be null or empty.";
        }
        if (keywords == null || keywords.isEmpty()) {
            return "*** Error *** Keywords cannot be null or empty.";
        }
        if (body == null || body.isEmpty()) {
            return "*** Error *** Body cannot be null or empty.";
        }
        if (references == null || references.isEmpty()) {
            return "*** Error *** References cannot be null or empty.";
        }

        // Attempt to add the article to the database
        try {
            id = databaseHelper.getArticleID(title);
            databaseHelper.createArticle(null, title, difficulty, authors, abstractText, keywords, body, references);
            articleAdded = true;
        } catch (Exception e) {
            return "*** Error *** Failed to add article: " + e.getMessage();
        }

        // Retrieve the article ID after addition
        try {
            id = databaseHelper.getArticleID(title);
        } catch (Exception e) {
            return "*** Error *** Failed to get ID: " + e.getMessage();
        }

        // List all articles in the database (for verification purposes)
        try {
            System.out.println(databaseHelper.listArticles());
        } catch (Exception e) {
            return "*** Error *** Failed to list articles: " + e.getMessage();
        }

        // Attempt to delete the article from the database
        try {
            databaseHelper.deleteArticle(id);
            articleDeleted = true;
        } catch (Exception e) {
            return "*** Error *** Failed to delete article: " + e.getMessage();
        }

        // List articles again to verify deletion
        try {
            System.out.println(databaseHelper.listArticles());
        } catch (Exception e) {
            return "*** Error *** Failed to list articles: " + e.getMessage();
        }

        return "";
    }
}
