package tests;

import org.junit.Before;
import org.junit.Test;

import edu.asu.DatabasePart1.DatabaseHelper;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class AdminRoleManagementTests {

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() throws Exception {
        // Initialize DatabaseHelper instance and set up a test database connection
        databaseHelper = new DatabaseHelper();
        databaseHelper.connectToDatabase();

        // Clean up and add a test user for a clean slate
        databaseHelper.deleteAllUsers(); // Clean up the user table
        databaseHelper.register(
        	    "testuser@example.com",       // email
        	    "TestUser",                   // username
        	    "testpassword",               // password
        	    "User",                       // role
        	    false,                        // otp (e.g., no OTP required)
        	    null,                         // otpExpiry (null if OTP not used)
        	    "Test",                       // firstName
        	    "",                           // middleName (empty string if not provided)
        	    "User",                       // lastName
        	    "TestUser",                   // preferredName
        	    "Beginner"                    // expertiseLevels (e.g., default expertise level)
        	);

    }

    @Test
    public void testAddRoleToUser() throws SQLException {
        String email = "testuser@example.com";
        String newRole = "admin";

        // Add a new role
        databaseHelper.addRoleToUser(email, newRole);

        // Retrieve roles and verify the new role was added
        String[] roles = databaseHelper.getUserRoles(email);
        assertNotNull("Roles should not be null", roles);
        assertTrue("Roles should contain the new role", containsRole(roles, newRole));
    }

    @Test
    public void testAddExistingRole() throws SQLException {
        String email = "testuser@example.com";
        String existingRole = "user";

        // Attempt to add an existing role
        databaseHelper.addRoleToUser(email, existingRole);

        // Retrieve roles and ensure the role was not duplicated
        String[] roles = databaseHelper.getUserRoles(email);
        assertNotNull("Roles should not be null", roles);
        assertEquals("Roles should not have duplicates", 1, countOccurrences(roles, existingRole));
    }

    @Test
    public void testGetUserRoles() throws SQLException {
        String email = "testuser@example.com";

        // Retrieve roles and verify the initial role
        String[] roles = databaseHelper.getUserRoles(email);
        assertNotNull("Roles should not be null", roles);
    }

    @Test
    public void testGetUserRolesForNonexistentUser() throws SQLException {
        String email = "nonexistent@example.com";

        // Attempt to retrieve roles for a non-existent user
        String[] roles = databaseHelper.getUserRoles(email);
        assertNotNull("Roles should not be null", roles);
        assertEquals("Roles should be an empty array", 0, roles.length);
    }

    // Helper Methods for Role Assertions

    private boolean containsRole(String[] roles, String role) {
        for (String r : roles) {
            if (r.trim().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    private int countOccurrences(String[] roles, String role) {
        int count = 0;
        for (String r : roles) {
            if (r.trim().equalsIgnoreCase(role)) {
                count++;
            }
        }
        return count;
    }
}
