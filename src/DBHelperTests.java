import static org.junit.Assert.*;

import org.junit.Test;

import edu.asu.DatabasePart1.DatabaseHelper;

import org.junit.Before;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class DBHelperTests {

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() throws SQLException {
        // Initialize DatabaseHelper instance and set up a test database connection
    	databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase(); // Ensure a database connection is set up
        databaseHelper.deleteAllGroups();
    }

    // The default rights for new instructors added to this group do not include admin rights for this group.
    @Test
    public void testNewInstructorsDoNotHaveAdminRightsByDefault() {
        try {
            // Set up test data for the group
            String groupId = "1";  // Group ID
            String groupName = "Test Group";
            String articleIds = "1,2,3";
            String admins = "1,2";  // Existing admins
            String instructors = "3,4,5";  // IDs for instructors
            String students = "6,7";  // IDs for students
            String type = "general";

            // Create the group with existing data
            databaseHelper.createGroup(groupId, groupName, articleIds, admins, instructors, students, type);

            // Add a new instructor using the updateGroupUsers method
            String newInstructorId = "8";
            databaseHelper.updateGroupUsers(groupId, "instructors", newInstructorId, true);

            // Verify the new instructor does not have admin rights by default
            boolean isNewInstructorAdmin = databaseHelper.isInstructorAdminInGroup(groupId, newInstructorId);

            // Assert that the new instructor should not have admin rights
            assertFalse("New instructor should not have admin rights by default", isNewInstructorAdmin);

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database operation failed due to SQL exception");
        }
        databaseHelper.deleteAllGroups();
    }
    

    // A list of instructors who have been given rights to view the decrypted bodies of articles in this group.
    @Test
    public void testInstructorsWithDecryptedArticleRights() {
        try {
            // Set up test data for the group
            String groupId = "1";  // ID for the group
            String groupName = "Test Group";
            String articleIds = "1,2,3";
            String admins = "1,2";  
            String instructors = "3,4,5";
            String students = "6,7"; 
            String type = "general";

            // Create the group in the database
            databaseHelper.createGroup(groupId, groupName, articleIds, admins, instructors, students, type);

            // Retrieve and verify instructors with rights to view decrypted bodies
            List<String> expectedInstructors = List.of("3", "4", "5");
            List<String> instructorsWithRights = new ArrayList<>();
            for (String instructor : expectedInstructors) {
                if (databaseHelper.isInstructorInGroup(groupId, instructor)) {
                    instructorsWithRights.add(instructor);
                }
            }

            // Verify the expected instructors are returned
            assertNotNull("Instructors list should not be null", instructorsWithRights);
            assertTrue("Instructors list should contain '3'", instructorsWithRights.contains("3"));
            assertTrue("Instructors list should contain '4'", instructorsWithRights.contains("4"));
            assertTrue("Instructors list should contain '5'", instructorsWithRights.contains("5"));

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database operation failed");
        }
        databaseHelper.deleteAllGroups();
    }
    
    
    // A list of instructors given admin rights for this group.
    @Test
    public void testInstructorsWithAdminRights() {
        try {
            // Set up test data for the group
            String groupId = "2";  // ID for the group
            String groupName = "Test Group 2";
            String articleIds = "4,5,6";
            String admins = "1,3";  
            String instructors = "1,3";
            String students = "8,9"; 
            String type = "general";

            // Create the group in the database
            databaseHelper.createGroup(groupId, groupName, articleIds, admins, instructors, students, type);

            // Retrieve and verify instructors with admin rights
            List<String> expectedAdmins = List.of("1", "3");
            List<String> instructorsWithAdminRights = new ArrayList<>();
            for (String instructor : expectedAdmins) {
                if (databaseHelper.isInstructorAdminInGroup(groupId, instructor)) {
                    instructorsWithAdminRights.add(instructor);
                }
            }

            // Verify the expected instructors are returned
            assertNotNull("Instructors list should not be null", instructorsWithAdminRights);
            assertTrue("Instructors list should contain '1'", instructorsWithAdminRights.contains("1"));
            assertTrue("Instructors list should contain '3'", instructorsWithAdminRights.contains("3"));

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database operation failed");
        }
        databaseHelper.deleteAllGroups();
    }

}
