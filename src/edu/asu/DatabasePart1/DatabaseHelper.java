package edu.asu.DatabasePart1;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
import javafx.scene.control.Alert;

/**
 * <p> DatabaseHelper Class. </p>
 * 
 * <p>  Description: Database helper class used to aid in the alteration of data 
 * within the applications database. Handles all connections and operations on database. </p>
 * 
 * <p> Copyright: Group 11 - CSE 360 © 2024 </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya Kar, Raya Khanna
 *  
 * @version 1.00 	2024-10-09 Project Phase 1 DatabaseHelper Page
 * @version 2.00 	2024-10-30 Project Phase 2 DatabaseHelper Page
 * 
 */

public class DatabaseHelper {
	

    /** JDBC driver name and database URL use for identifying portions of database access.*/
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/firstDatabase";

    /** Holds Database Credentials which allow further access. */
    static final String USER = "sa";
    static final String PASS = "";

    /** Handles connections to the database and keeps updated on their status */
    private Connection connection = null;
    private Statement statement = null;
    
    private EncryptionHelper encryptionHelper;
	
 

    // Ensures the database connection is established correctly 
    public void ensureConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            connectToDatabase();
        }
    }

    // Connects and establishes database setup
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); 		// Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            // Create the necessary tables if they don't exist
            createTables(); 
            createArticleTables();
            try {
				encryptionHelper = new EncryptionHelper();
			} catch (Exception e) {
				e.printStackTrace();
			}
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    // Creates storage for users in the database in an organized way
    private void createTables() throws SQLException {
        ensureConnection(); // Ensure connection is available

        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "email VARCHAR(255), "
                + "username VARCHAR(50), "
                + "password BLOB, " // Store hashed passwords
                + "otp BOOLEAN, " // Indicates if the password is a one-time password
                + "otp_expiry TIMESTAMP, " // Expiration time for the OTP
                + "first_name VARCHAR(50), "
                + "middle_name VARCHAR(50), "
                + "last_name VARCHAR(50), "
                + "preferred_name VARCHAR(50), "
                + "roles VARCHAR(255), " // Store multiple roles in a comma-separated string
                + "invite_code VARCHAR(255), " // One-time invitation code
                + "java_level VARCHAR(255), " // Store expertise levels as a string (e.g., beginner, intermediate)
        		+ "javaFX_level VARCHAR(255), " // Store expertise levels as a string (e.g., beginner, intermediate)
        		+ "github_level VARCHAR(255))"; // Store expertise levels as a string (e.g., beginner, intermediate)
        statement.execute(userTable);
    }

    // Securely hash the password using SHA-256
    private byte[] hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage());
        }
    }

    // Register a new user or the first Admin user
    public void register(String email, String username, String password, String role, boolean otp, Timestamp otpExpiry,
                         String firstName, String middleName, String lastName, String preferredName, String expertiseLevels) throws SQLException {
        ensureConnection(); // Ensure connection is available

        // Check if this is the first user, make them an Admin
        if (isDatabaseEmpty()) {
            role = "Admin";
        }
        
        // Stores roles in the database
        String insertUser = "INSERT INTO cse360users (email, username, password, otp, otp_expiry, first_name, middle_name, last_name, preferred_name, roles) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, email);
            pstmt.setString(2, username);
            pstmt.setBytes(3, hashPassword(password)); // Store hashed password
            pstmt.setBoolean(4, otp);
            pstmt.setTimestamp(5, otpExpiry);
            pstmt.setString(6, firstName);
            pstmt.setString(7, middleName);
            pstmt.setString(8, lastName);
            pstmt.setString(9, preferredName);
            pstmt.setString(10, role); // Store roles (can be multiple, comma-separated)
            pstmt.executeUpdate();
        }
    }
    
    // Check if the email already exists 
    public boolean emailExists(String email) throws SQLException {
    	ensureConnection(); // Ensure connection is available
    	String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
            	System.out.println(rs);
                if (rs.next()) {
                    return rs.getInt(1) != 0;
                }
            }
        }
        return false;
    }
    
    // Check if the username already exists 
    public boolean usernameExists(String username) throws SQLException {
    	ensureConnection(); // Ensure connection is available
    	String query = "SELECT COUNT(*) FROM cse360users WHERE username = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
            	System.out.println(rs);
                if (rs.next()) {
                    return rs.getInt(1) != 0;
                }
            }
        }
        return false;
    }

    // Check if the database is empty (i.e., no users are registered)
    public boolean isDatabaseEmpty() throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT COUNT(*) FROM cse360users";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return false;
    }

    // Login with password validation
    public boolean login(String email, String password) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT * FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    byte[] storedHash = rs.getBytes("password");
                    return MessageDigest.isEqual(storedHash, hashPassword(password)); // Return true if the password matches
                }
            }
        }
        return false; // Login failed
    }

    // Generate a one-time invitation code
    public String generateInvitationCode(String role) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String inviteCode = UUID.randomUUID().toString();
        String insertInvite = "INSERT INTO cse360users (invite_code, roles) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertInvite)) {
            pstmt.setString(1, inviteCode);
            pstmt.setString(2, role);
            pstmt.executeUpdate();
        }
        return inviteCode;
    }

    // Register a user with an invitation code
    public boolean registerWithInvitationCode(String inviteCode, String username, String password) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT * FROM cse360users WHERE invite_code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String roles = rs.getString("roles");
                    String updateUser = "UPDATE cse360users SET username = ?, password = ?, invite_code = NULL WHERE invite_code = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateUser)) {
                        updateStmt.setString(1, username);
                        updateStmt.setBytes(2, hashPassword(password));
                        updateStmt.setString(3, inviteCode);
                        updateStmt.executeUpdate();
                    }
                    return true; // Successfully registered with the invitation code
                }
            }
        }
        return false; // Invalid invitation code
    }
    
    // User: Set new password
    public void setNewPassword(String email, String newPassword) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String updateUser = "UPDATE cse360users SET password = ?, otp = FALSE WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
            pstmt.setBytes(1, hashPassword(newPassword));
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }

    // Admin: Reset a user's account
    public void resetUserAccount(String email, String newPassword, Timestamp expiry) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String updateUser = "UPDATE cse360users SET password = ?, otp = TRUE, otp_expiry = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
            pstmt.setBytes(1, hashPassword(newPassword));
            pstmt.setTimestamp(2, expiry);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        }
    }

    // Admin: Delete a user account
    public void deleteUserAccount(String email) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String deleteUser = "DELETE FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
            pstmt.setString(1, email);
            pstmt.executeUpdate();
        }
    }


    // Admin: Add a role to a user
    public void addRoleToUser(String email, String newRole) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT roles FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String roles = rs.getString("roles");
                    if (!roles.contains(newRole)) {
                        roles = roles + "," + newRole; // Append new role
                        updateRoles(email, roles);
                    }
                }
            }
        }
    }

    // Updates the roles for the user 
    private void updateRoles(String email, String roles) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String updateQuery = "UPDATE cse360users SET roles = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, roles);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }
    
 // Check if the password is a one-time password for the given user
    public boolean isOneTimePassword(String email) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT otp FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("otp"); // Return true if it is a one-time password
                }
            }
        }
        return false;
    }

    // Check if the user's account setup is complete (e.g., has provided name and other required details)
    public boolean isAccountSetupComplete(String email) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT first_name, last_name FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    // Account setup is considered complete if both first and last names are filled in
                    return firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty();
                }
            }
        }
        return false;
    }

    // Check if the user has multiple roles assigned
    public boolean hasMultipleRoles(String email) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT roles FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String roles = rs.getString("roles");
                    // Split the roles string by commas and check if there are multiple roles
                    String[] rolesArray = roles.split(",");
                    return rolesArray.length > 1;
                }
            }
        }
        return false;
    }

    // Get the user's role (only if the user has one role)
    public String getUserRole(String email) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT roles FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String roles = rs.getString("roles");
                    // Return the role if there's only one role
                    String[] rolesArray = roles.split(",");
                    if (rolesArray.length == 1) {
                        return rolesArray[0];
                    }
                }
            }
        }
        return null; // Return null if multiple roles or no role found
    }
 // Get the user's roles as an array of strings
    public String[] getUserRoles(String email) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT roles FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String roles = rs.getString("roles");
                    // Split the roles string by commas and return as an array
                    return roles.split(",");
                }
            }
        }
        return new String[0]; // Return an empty array if no roles are found
    }
 // Update the user's account with the provided details
    public void updateUserAccount(String email, String firstName, String middleName, String lastName, String preferredName, String javaLevel, String javaFXLevel, String githubLevel) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String updateQuery = "UPDATE cse360users SET first_name = ?, middle_name = ?, last_name = ?, preferred_name = ?, java_level = ?, javaFX_level = ?, github_level = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, middleName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, preferredName);
            pstmt.setString(5, javaFXLevel);
            pstmt.setString(6, javaLevel);
            pstmt.setString(7, githubLevel);
            pstmt.setString(8, email);
            pstmt.executeUpdate();
        }
    }
    // List all user accounts with their details
    public String listUserAccounts() throws SQLException {
        ensureConnection(); // Ensure the connection is established

        StringBuilder usersList = new StringBuilder();
        String query = "SELECT username, first_name, middle_name, last_name, roles FROM cse360users";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String username = rs.getString("username");
                String firstName = rs.getString("first_name");
                String middleName = rs.getString("middle_name");
                String lastName = rs.getString("last_name");
                String roles = rs.getString("roles");

                // Append user details to the list
                usersList.append("Username: ").append(username).append("\n")
                         .append("Name: ").append(firstName);

                if (middleName != null && !middleName.isEmpty()) {
                    usersList.append(" ").append(middleName);
                }

                usersList.append(" ").append(lastName).append("\n")
                         .append("Roles: ").append(roles).append("\n")
                         .append("-------------------------------\n");
            }
        }

        if (usersList.length() == 0) {
            return "No user accounts found.";
        } else {
            return usersList.toString();
        }
    }
    // Remove a role from a user
    public void removeRoleFromUser(String email, String roleToRemove) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT roles FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String roles = rs.getString("roles");
                    // Split roles into an array, filter out the role to remove, and join them back
                    String[] rolesArray = roles.split(",");
                    StringBuilder updatedRoles = new StringBuilder();

                    for (String role : rolesArray) {
                        if (!role.trim().equalsIgnoreCase(roleToRemove)) {
                            if (updatedRoles.length() > 0) {
                                updatedRoles.append(",");
                            }
                            updatedRoles.append(role.trim());
                        }
                    }

                    // Ensure the user has at least one role left
                    if (updatedRoles.length() == 0) {
                        throw new SQLException("User must have at least one role.");
                    }

                    // Update the roles in the database
                    updateRoles(email, updatedRoles.toString());
                } else {
                    throw new SQLException("User with email " + email + " not found.");
                }
            }
        }
    }
    // Check if a user has a specific role
    public boolean hasRole(String email, String role) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String query = "SELECT roles FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String roles = rs.getString("roles");
                    String[] rolesArray = roles.split(",");
                    for (String r : rolesArray) {
                        if (r.trim().equalsIgnoreCase(role)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    // Update the user's email after registration
    public void updateUserEmail(String username, String email) throws SQLException {
        ensureConnection(); // Ensure connection is available

        String updateQuery = "UPDATE cse360users SET email = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, email);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }

            


    // Closes the connection with the database 
    public void closeConnection() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if (connection != null) connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    
    /**
     * Creates the articles table if it does not already exist.
     * 
     * @throws SQLException If a database access error occurs.
     */
    private void createArticleTables() throws SQLException {
        String articleTable = "CREATE TABLE IF NOT EXISTS articles ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "title VARCHAR(255), "
                + "difficulty VARCHAR(255),"
                + "authors VARCHAR(255), "
                + "abstract VARCHAR(255), "
                + "keywords VARCHAR(255), "
                + "body TEXT, "
                + "references VARCHAR(255))";
        statement.execute(articleTable);
    }
    
    /**
     * Creates a new article in the database.
     * 
     * @param title The title of the article.
     * @param difficulty The difficulty level of the article.
     * @param authors The authors of the article.
     * @param abstractText The abstract of the article.
     * @param keywords The keywords associated with the article.
     * @param body The body content of the article.
     * @param references The references for the article.
     * @throws Exception If an error occurs while creating the article.
     */
    public void createArticle(String title, String difficulty, String authors, 
            String abstractText, String keywords, String body, String references) throws Exception {
        
        String insertArticle = "INSERT INTO articles (title, difficulty, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
            pstmt.setString(1, title);
            pstmt.setString(2, difficulty);
            pstmt.setString(3, authors);
            pstmt.setString(4, abstractText);
            pstmt.setString(5, keywords);
            pstmt.setString(6, body);
            pstmt.setString(7, references);
            pstmt.executeUpdate();
        }
        System.out.println("Article created successfully.");
    }


    /**
     * Displays all articles from the database.
     * 
     * @throws Exception If an error occurs while retrieving or displaying the articles.
     */
    public void displayArticles() throws Exception {
        String sql = "SELECT * FROM articles"; 
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String authors = rs.getString("authors");
            String abstractText = rs.getString("abstract");
            String keywords = rs.getString("keywords");
            String body = rs.getString("body");
            String references = rs.getString("references");
            
            System.out.println("ID: " + id);
            System.out.println("Title: " + title);
            System.out.println("Authors: " + authors);
            System.out.println("Abstract: " + abstractText);
            System.out.println("Keywords: " + keywords);
            System.out.println("Body: " + body);
            System.out.println("References: " + references);
            System.out.println();
        }
    }
    
    /**
     * Retrieves the ID of an article based on its title.
     *
     * @param title The title of the article whose ID is to be retrieved.
     * @return      The ID of the article if found, or -1 if the article does not exist.
     * @throws SQLException If an error occurs while accessing the database.
     */
    public int getArticleID(String title) throws SQLException {
        ensureConnection(); // Ensure a valid database connection

        String query = "SELECT id FROM articles WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, title);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id"); // Return the article ID if found
                }
            }
        }
        return -1; // Return -1 if no article with the specified title is found
    }
    
    /**
     * Retrieves a list of all articles in the database, including their details.
     * Each article includes its ID, title, difficulty level, authors, abstract, keywords, body, and references.
     * 
     * @return A formatted string containing the details of each article, or a message indicating no articles were found.
     * @throws SQLException If an error occurs while retrieving articles from the database.
     */
    public String listArticles() throws SQLException {
        ensureConnection(); // Ensure the connection is established

        StringBuilder articlesList = new StringBuilder();
        String query = "SELECT id, title, difficulty, authors, abstract, keywords, body, references FROM articles";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String difficulty = rs.getString("difficulty"); // Retrieve difficulty
                String author = rs.getString("authors");
                String abstractVal = rs.getString("abstract");
                String keywords = rs.getString("keywords");
                String body = rs.getString("body");
                String references = rs.getString("references");

                // Append article details to the list
                articlesList.append("ID: ").append(id).append("\n")
                    .append("Title: ").append(title).append("\n")
                    .append("Difficulty: ").append(difficulty).append("\n") // Append difficulty
                    .append("Author: ").append(author).append("\n")
                    .append("Abstract: ").append(abstractVal).append("\n")
                    .append("Keywords: ").append(keywords).append("\n")
                    .append("Body: ").append(body).append("\n")
                    .append("References: ").append(references).append("\n")
                    .append("-------------------------------\n");
            }
        }

        if (articlesList.length() == 0) {
            return "No articles found.";
        } else {
            return articlesList.toString();
        }
    }


    /**
     * Deletes an article from the database by its ID.
     * 
     * @param id The ID of the article to be deleted.
     * @throws Exception If an error occurs while deleting the article.
     */
    public void deleteArticle(int id) throws Exception {
        String sql = "DELETE FROM articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Article deleted successfully.");
            } else {
                System.out.println("No article found with that ID.");
            }
        }
    }

    /**
     * Backs up all articles to a specified file with encryption.
     * 
     * @param fileName The name of the file where the backup will be stored.
     * @throws Exception If an error occurs during the backup process.
     */
    public void backupArticles(String fileName) throws Exception {
        String sql = "SELECT * FROM articles"; 
        ResultSet rs = statement.executeQuery(sql);

        List<String> articles = new ArrayList<>();
        while (rs.next()) {
            String title = rs.getString("title");
            String difficulty = rs.getString("difficulty");  // New difficulty field
            String authors = rs.getString("authors");
            String abstractText = rs.getString("abstract");
            String keywords = rs.getString("keywords");
            String body = rs.getString("body");
            String references = rs.getString("references");
            articles.add(title + "|" + difficulty + "|" + authors + "|" + abstractText + "|" + keywords + "|" + body + "|" + references);
        }

        byte[] plainText = String.join("\n", articles).getBytes();
        byte[] initializationVector = EncryptionUtils.getInitializationVector("this-is-our-secret".toCharArray());
        byte[] encryptedData = encryptionHelper.encrypt(plainText, initializationVector);
        
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(initializationVector);
            fos.write(encryptedData);
        }
        System.out.println("Backup created successfully.");
    }


    /**
     * Restores articles from a specified backup file.
     * 
     * @param fileName The name of the file from which the articles will be restored.
     * @throws Exception If an error occurs during the restoration process.
     */
    public void restoreArticles(String fileName) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Backup file not found.");
            return;
        }

        byte[] initializationVector = new byte[16];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(initializationVector);
            byte[] encryptedData = new byte[(int) (file.length() - initializationVector.length)];
            fis.read(encryptedData);

            byte[] decryptedData = encryptionHelper.decrypt(encryptedData, initializationVector);
            String[] articles = new String(decryptedData).split("\n");

            // Clear existing articles
            statement.executeUpdate("DELETE FROM articles");

            for (String article : articles) {
                String[] fields = article.split("\\|");
                if (fields.length == 7) {
                    createArticle(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
                }
            }
        }
        System.out.println("Restore completed successfully.");
    }
}

