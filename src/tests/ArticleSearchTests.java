package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import edu.asu.DatabasePart1.DatabaseHelper;

import java.sql.SQLException;

public class ArticleSearchTests {

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() throws Exception {
        // Initialize DatabaseHelper instance and set up a test database connection
        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase();
        databaseHelper.deleteAllArticles(); // Clear all articles for a clean slate

        
        databaseHelper.createArticle(null, "Introduction to Java", "Easy", "John Doe", "Learn Java basics",
                "java, programming", "Body content about Java", "Reference 1");
        databaseHelper.createArticle(null, "Advanced SQL Techniques", "Medium", "Jane Smith", "Master SQL techniques",
                "sql, database", "Body content about SQL", "Reference 2");
        databaseHelper.createArticle(null, "Machine Learning Basics", "Hard", "Alice Johnson", "Intro to ML concepts",
                "machine learning, ai", "Body content about ML", "Reference 3");
    }

    @Test
    public void testSearchWithValidQuery() throws SQLException {
        int[] idList = {1, 2, 3}; // Assume auto-generated IDs are sequential
        String searchQuery = "java";

        int[] result = databaseHelper.searchForArticles(searchQuery, idList);

        assertNotNull("Result should not be null", result);
        assertEquals("Should return 1 matching article", 1, result.length);
        assertEquals("Matching article ID should be 1", 1, result[0]);
    }

    @Test
    public void testSearchWithNoMatch() throws SQLException {
        int[] idList = {1, 2, 3};
        String searchQuery = "python";

        int[] result = databaseHelper.searchForArticles(searchQuery, idList);

        assertNotNull("Result should not be null", result);
        assertEquals("Should return 0 matches", 0, result.length);
    }

    @Test
    public void testSearchWithNullQueryReturnsAllIDs() throws SQLException {
        int[] idList = {1, 2, 3};

        int[] result = databaseHelper.searchForArticles(null, idList);

        assertNotNull("Result should not be null", result);
        assertArrayEquals("Should return all IDs when query is null", idList, result);
    }

    @Test
    public void testSearchCaseInsensitive() throws SQLException {
        int[] idList = {1, 2, 3};
        String searchQuery = "SQL";

        int[] result = databaseHelper.searchForArticles(searchQuery, idList);

        assertNotNull("Result should not be null", result);
        assertEquals("Should return 1 matching article", 1, result.length);
        assertEquals("Matching article ID should be 2", 2, result[0]);
    }
}
