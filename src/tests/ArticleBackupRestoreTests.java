package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.asu.DatabasePart1.DatabaseHelper;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class ArticleBackupRestoreTests {

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() throws SQLException {
        // Initialize databaseHelper
        databaseHelper = new DatabaseHelper();

        // Connect to the database and ensure a clean slate
        databaseHelper.connectToDatabase();
        databaseHelper.deleteAllArticles(); // Assumes this method clears the articles table
    }

    @Test
    public void testBackupAndRestoreAllArticles() {
        try {
            // Insert test data
            databaseHelper.createArticle("1", "Test Title 1", "Easy", "Author1", "Abstract1", "keyword1", "Body1", "Ref1");
            databaseHelper.createArticle("2", "Test Title 2", "Medium", "Author2", "Abstract2", "keyword2", "Body2", "Ref2");

            // Backup articles to a file
            String backupFile = "all_articles_backup.enc";
            databaseHelper.backupArticles(backupFile);

            // Clear the articles table
            databaseHelper.deleteAllArticles();
            assertTrue("Articles table should be empty", databaseHelper.getAllArticles().isEmpty());

            // Restore articles from the backup file
            databaseHelper.restoreArticles(backupFile);

            // Verify restored data
            List<String[]> articles = databaseHelper.getAllArticles();
            assertEquals("Two articles should be restored", 2, articles.size());
            assertEquals("Restored article ID should match", "1", articles.get(0)[0]);
            assertEquals("Restored article ID should match", "2", articles.get(1)[0]);

            // Clean up
            new File(backupFile).delete();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during backup and restore testing");
        }
    }

    @Test
    public void testBackupAndRestoreByKeyword() {
        try {
            // Insert test data
            databaseHelper.createArticle("1", "Test Title 1", "Easy", "Author1", "Abstract1", "keyword1,keyword2", "Body1", "Ref1");
            databaseHelper.createArticle("2", "Test Title 2", "Medium", "Author2", "Abstract2", "keyword3", "Body2", "Ref2");

            // Backup articles with a specific keyword
            String keywordBackupFile = "keyword_articles_backup.enc";
            databaseHelper.backupByKeyword(keywordBackupFile, "keyword1");

            // Clear the articles table
            databaseHelper.deleteAllArticles();
            assertTrue("Articles table should be empty", databaseHelper.getAllArticles().isEmpty());

            // Restore articles from the keyword backup file
            databaseHelper.restoreArticles(keywordBackupFile);

            // Verify restored data
            List<String[]> articles = databaseHelper.getAllArticles();
            assertEquals("One article should be restored", 1, articles.size());
            assertEquals("Restored article ID should match", "1", articles.get(0)[0]);

            // Clean up
            new File(keywordBackupFile).delete();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during keyword backup and restore testing");
        }
    }
}
