package tests;
import static org.junit.Assert.*;

import org.junit.Test;

import edu.asu.DatabasePart1.DatabaseHelper;

import org.junit.Before;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class DBHelperBackupRestoreTests {

    private DatabaseHelper databaseHelper;
    private static final String BACKUP_FILE = "test_backup.enc";

    @Before
    public void setUp() throws SQLException {
        // Initialize DatabaseHelper instance and set up a test database connection
        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase();
        databaseHelper.deleteAllGroups(); // Clear the database before each test
    }

    @Test
    public void testBackupGroupsCreatesEncryptedFile() {
        try {
            // Set up test data for the database
            String groupId = "1";
            String groupName = "Backup Test Group";
            String articleIds = "1,2,3";
            String admins = "1,2";
            String instructors = "3,4,5";
            String students = "6,7";
            String type = "general";

            // Create a test group in the database
            databaseHelper.createGroup(groupId, groupName, articleIds, admins, instructors, students, type);

            // Call the backup method
            databaseHelper.backupGroups(BACKUP_FILE);

            // Verify that the backup file was created
            File backupFile = new File(BACKUP_FILE);
            assertTrue("Backup file should exist", backupFile.exists());
            assertTrue("Backup file should not be empty", backupFile.length() > 0);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Backup operation failed");
        } finally {
            // Clean up
            new File(BACKUP_FILE).delete();
            databaseHelper.deleteAllGroups();
        }
    }

    @Test
    public void testRestoreGroupsPopulatesDatabaseFromBackup() {
        try {
            // Set up test data for the database
            String groupId = "1";
            String groupName = "Restore Test Group";
            String articleIds = "4,5,6";
            String admins = "2,3";
            String instructors = "4,5,6";
            String students = "7,8";
            String type = "special";

            // Create a test group and back it up
            databaseHelper.createGroup(groupId, groupName, articleIds, admins, instructors, students, type);
            databaseHelper.backupGroups(BACKUP_FILE);

            // Clear the database and verify it is empty
            databaseHelper.deleteAllGroups();
            List<String> groups = databaseHelper.getAllGroupIds();
            assertTrue("Database should be empty after deletion", groups.isEmpty());

            // Restore the group from the backup
            databaseHelper.restoreGroups(BACKUP_FILE);

            // Verify the group has been restored
            groups = databaseHelper.getAllGroupIds();
            assertFalse("Database should not be empty after restoration", groups.isEmpty());
            assertTrue("Restored group should match the original group ID", groups.contains(groupId));

        } catch (Exception e) {
            e.printStackTrace();
            fail("Restore operation failed");
        } finally {
            // Clean up
            new File(BACKUP_FILE).delete();
            databaseHelper.deleteAllGroups();
        }
    }

    @Test
    public void testRestoreFromMissingFile() {
        try {
            // Attempt to restore from a non-existent file
            databaseHelper.restoreGroups("non_existent_file.enc");

            // Ensure no exception occurs and the database remains empty
            List<String> groups = databaseHelper.getAllGroupIds();
            assertTrue("Database should remain empty after restore from missing file", groups.isEmpty());
        } catch (Exception e) {
            fail("Restore operation should handle missing file gracefully");
        }
    }

    @Test
    public void testBackupGroupsHandlesEmptyDatabase() {
        try {
            // Ensure database is empty
            databaseHelper.deleteAllGroups();

            // Backup an empty database
            databaseHelper.backupGroups(BACKUP_FILE);

            // Verify that the backup file exists and contains only metadata
            File backupFile = new File(BACKUP_FILE);
            assertTrue("Backup file should exist", backupFile.exists());
            assertTrue("Backup file should not be empty", backupFile.length() > 0);

            // Verify file content is encrypted
            try (FileInputStream fis = new FileInputStream(backupFile)) {
                byte[] fileContent = new byte[(int) backupFile.length()];
                fis.read(fileContent);
                String fileText = new String(fileContent);
                assertFalse("File content should be encrypted", fileText.contains("groups"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("Backup operation for empty database failed");
        } finally {
            // Clean up
            new File(BACKUP_FILE).delete();
        }
    }
}
