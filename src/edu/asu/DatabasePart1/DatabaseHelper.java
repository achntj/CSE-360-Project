package edu.asu.DatabasePart1;

import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
import javafx.scene.control.Alert;

/**
 * <p>
 * DatabaseHelper Class.
 * </p>
 * 
 * <p>
 * Description: Database helper class used to aid in the alteration of data
 * within the applications database. Handles all connections and operations on
 * database.
 * </p>
 * 
 * <p>
 * Copyright: Group 11 - CSE 360 © 2024
 * </p>
 * 
 * @author Achintya Jha, Akshin Senthilkumar, Ridham Ashwinkumar Patel, Shreeya
 *         Kar, Raya Khanna
 * 
 * @version 1.00 2024-10-09 Project Phase 1 DatabaseHelper Page
 * @version 2.00 2024-10-30 Project Phase 2 DatabaseHelper Page
 * 
 */

public class DatabaseHelper {

	/**
	 * JDBC driver name and database URL use for identifying portions of database
	 * access.
	 */
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
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();
			// Create the necessary tables if they don't exist
			createTables();
			createArticleTables();
			createMessagesTables();
			createGroupsTable();
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

		String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "email VARCHAR(255), " + "username VARCHAR(50), " + "password BLOB, " // Store hashed passwords
				+ "otp BOOLEAN, " // Indicates if the password is a one-time password
				+ "otp_expiry TIMESTAMP, " // Expiration time for the OTP
				+ "first_name VARCHAR(50), " + "middle_name VARCHAR(50), " + "last_name VARCHAR(50), "
				+ "preferred_name VARCHAR(50), " + "roles VARCHAR(255), " // Store multiple roles in a comma-separated
																			// string
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
			String firstName, String middleName, String lastName, String preferredName, String expertiseLevels)
			throws SQLException {
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
					return MessageDigest.isEqual(storedHash, hashPassword(password)); // Return true if the password
																						// matches
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

	// Check if the user's account setup is complete (e.g., has provided name and
	// other required details)
	public boolean isAccountSetupComplete(String email) throws SQLException {
		ensureConnection(); // Ensure connection is available

		String query = "SELECT first_name, last_name FROM cse360users WHERE email = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String firstName = rs.getString("first_name");
					String lastName = rs.getString("last_name");
					// Account setup is considered complete if both first and last names are filled
					// in
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
	public void updateUserAccount(String email, String firstName, String middleName, String lastName,
			String preferredName, String javaLevel, String javaFXLevel, String githubLevel) throws SQLException {
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
		String query = "SELECT id, email, username, middle_name, last_name, roles FROM cse360users";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int id = rs.getInt("id");
				String email = rs.getString("email");
				String username = rs.getString("username");
				String firstName = getName(email);
				String middleName = rs.getString("middle_name");
				String lastName = rs.getString("last_name");
				String roles = rs.getString("roles");

				// Append user details to the list

				usersList.append("ID: " + id + "\n");
				usersList.append("Username: ").append(username).append("\n").append("Name: ").append(firstName);

				if (middleName != null && !middleName.isEmpty()) {
					usersList.append(" ").append(middleName);
				}

				usersList.append(" ").append(lastName).append("\n").append("Roles: ").append(roles).append("\n")
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
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
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
		String articleTable = "CREATE TABLE IF NOT EXISTS articles (" + "id INT AUTO_INCREMENT, "
				+ "title VARCHAR(255), " + "difficulty VARCHAR(255)," + "authors VARCHAR(255), "
				+ "abstract VARCHAR(255), " + "keywords VARCHAR(255), " + "body TEXT, " + "references VARCHAR(255))";
		statement.execute(articleTable);
	}

	/**
	 * Creates a new article in the database.
	 * 
	 * @param title        The title of the article.
	 * @param difficulty   The difficulty level of the article.
	 * @param authors      The authors of the article.
	 * @param abstractText The abstract of the article.
	 * @param keywords     The keywords associated with the article.
	 * @param body         The body content of the article.
	 * @param references   The references for the article.
	 * @throws Exception If an error occurs while creating the article.
	 */
	public void createArticle(String id, String title, String difficulty, String authors, String abstractText,
			String keywords, String body, String references) throws Exception {
		System.out.println("Create function was called");

		if (id != null) {
			System.out.println("ID PROVIDED");
			String insertArticle = "INSERT INTO articles (id, title, difficulty, authors, abstract, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
				pstmt.setString(1, id);
				pstmt.setString(2, title);
				pstmt.setString(3, difficulty);
				pstmt.setString(4, authors);
				pstmt.setString(5, abstractText);
				pstmt.setString(6, keywords);
				pstmt.setString(7, body);
				pstmt.setString(8, references);
				pstmt.executeUpdate();
				System.out.println(pstmt);
			}
		} else {
			System.out.println("NO ID PROVIDED");
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
				System.out.println(pstmt);
			}
		}

		System.out.println("Article created successfully.");
	}

	/**
	 * Displays all articles from the database.
	 * 
	 * @throws Exception If an error occurs while retrieving or displaying the
	 *                   articles.
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
	 * @return The ID of the article if found, or -1 if the article does not exist.
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
	 * Each article includes its ID, title, difficulty level, authors, abstract,
	 * keywords, body, and references.
	 * 
	 * @return A formatted string containing the details of each article, or a
	 *         message indicating no articles were found.
	 * @throws SQLException If an error occurs while retrieving articles from the
	 *                      database.
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
				String references = rs.getString("references");

				// Append article details to the list
				articlesList.append("ID: ").append(id).append("\n").append("Title: ").append(title).append("\n")
						.append("Difficulty: ").append(difficulty).append("\n") // Append difficulty
						.append("Author: ").append(author).append("\n").append("Abstract: ").append(abstractVal)
						.append("\n").append("Keywords: ").append(keywords).append("\n").append("References: ").append(references).append("\n")
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

			String id = rs.getString("id");
			String title = rs.getString("title");
			String difficulty = rs.getString("difficulty"); // New difficulty field
			String authors = rs.getString("authors");
			String abstractText = rs.getString("abstract");
			String keywords = rs.getString("keywords");
			String body = rs.getString("body");
			String references = rs.getString("references");
			articles.add(id + "|" + title + "|" + difficulty + "|" + authors + "|" + abstractText + "|" + keywords + "|"
					+ body + "|" + references);
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
	 * Backs up articles that are under a specific keyword.
	 * 
	 * @param fileName The name of the file where the backup will be stored.
	 * @param keyword  The key that is being used to determine what to backup.
	 * @throws Exception If an error occurs during the backup process.
	 */

	public void backupByKeyword(String fileName, String keyword) throws Exception {
		String sql = "SELECT * FROM articles";
		ResultSet rs = statement.executeQuery(sql);

		List<String> articles = new ArrayList<>();
		String[] keywords = keyword.split(",");

		while (rs.next()) {
			String keyWords = rs.getString("keywords").toLowerCase();
			boolean hasKeyword = false;

			for (String kw : keywords) {
				kw = kw.trim().toLowerCase();
				if (keyWords.contains(kw)) {
					hasKeyword = true;
					break;
				}
			}

			if (hasKeyword) {
				String id = rs.getString("id");
				String title = rs.getString("title");
				String difficulty = rs.getString("difficulty"); // New difficulty field
				String authors = rs.getString("authors");
				String abstractText = rs.getString("abstract");
				String body = rs.getString("body");
				String references = rs.getString("references");
				articles.add(id + "|" + title + "|" + difficulty + "|" + authors + "|" + abstractText + "|" + keyWords
						+ "|" + body + "|" + references);
			}
		}

		byte[] plainText = String.join("\n", articles).getBytes();
		byte[] initializationVector = EncryptionUtils.getInitializationVector("this-is-our-secret".toCharArray());
		byte[] encryptedData = encryptionHelper.encrypt(plainText, initializationVector);

		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.write(initializationVector);
			fos.write(encryptedData);
		}

		System.out.println("Backup for keyword(s) " + keyword + " created successfully.");
	}

	/**
	 * Restores articles from a specified backup file.
	 * 
	 * @param fileName The name of the file from which the articles will be
	 *                 restored.
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
				System.out.println("Restoring this - " + article);
				createArticle(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7]);
			}
		}
		System.out.println("Restore completed successfully.");
	}

	/**
	 * Restores articles from a specified backup file.
	 * 
	 * @param fileName The name of the file from which the articles will be
	 *                 restored.
	 * @throws Exception If an error occurs during the restoration process.
	 */
	public void restoreArticlesByKeyword(String fileName, String keyword) throws Exception {
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
			statement.executeUpdate("DELETE FROM articles WHERE keywords LIKE '%" + keyword + "%'");

			for (String article : articles) {
				String[] fields = article.split("\\|");
				createArticle(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7]);
			}
		}
		System.out.println("Restore completed successfully.");
	}

	/**
	 * Creates the messages table if it does not already exist.
	 * 
	 * @throws SQLException If a database access error occurs.
	 */
	private void createMessagesTables() throws SQLException {
		String messageTable = "CREATE TABLE IF NOT EXISTS messages (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "email VARCHAR(255)," + "body TEXT, " + "type VARCHAR(255))";
		statement.execute(messageTable);
	}

	/**
	 * Creates a new message in the database.
	 * 
	 * @param email The email of the user who sent the message.
	 * @param body  The body content of the message.
	 * @param type  The type of the message - either generic or specific
	 * @throws Exception If an error occurs while creating the article.
	 */
	public void sendMessage(String email, String body, String type) throws Exception {

		String insertMessage = "INSERT INTO messages (email, body, type) VALUES (?, ?, ?)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertMessage)) {
			pstmt.setString(1, email);
			pstmt.setString(2, body);
			pstmt.setString(3, type);
			pstmt.executeUpdate();
		}
		System.out.println("Message sent successfully.");
	}

	/**
	 * Creates the groups table if it does not already exist.
	 * 
	 * @throws SQLException If a database access error occurs.
	 */
	private void createGroupsTable() throws SQLException {
	    String groupsTable = "CREATE TABLE IF NOT EXISTS groups ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "     // Unique identifier for the group
	            + "group_name VARCHAR(255), "               // Name of the group
	            + "article_ids VARCHAR(255), "              // Comma-separated list of article IDs in the group
	            + "admins VARCHAR(255), "                   // Comma-separated list of admin user IDs
	            + "instructors VARCHAR(255), "              // Comma-separated list of instructor user IDs
	            + "students VARCHAR(255), "                 // Comma-separated list of student user IDs
	    		+ "type VARCHAR(255))";						// type - either general or special
	    statement.execute(groupsTable);
	}

	/**
	 * Inserts a new group into the groups table.
	 * 
	 * @param id          The ID of the group (can be null).
	 * @param groupName   The name of the group.
	 * @param articleIds  Comma-separated list of article IDs in the group.
	 * @param admins      Comma-separated list of admin user IDs.
	 * @param instructors Comma-separated list of instructor user IDs.
	 * @param students    Comma-separated list of student user IDs.
	 * @param type        - either general or special access group
	 * @throws SQLException If a database access error occurs.
	 */
	public void createGroup(String id, String groupName, String articleIds, String admins, String instructors,
			String students, String type) throws SQLException {
		System.out.println("Create Group function was called");

		if (id != null) {
			System.out.println("ID PROVIDED");
			String insertGroup = "INSERT INTO groups (id, group_name, article_ids, admins, instructors, students, type) VALUES (?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertGroup)) {
				pstmt.setString(1, id);
				pstmt.setString(2, groupName);
				pstmt.setString(3, articleIds);
				pstmt.setString(4, admins);
				pstmt.setString(5, instructors);
				pstmt.setString(6, students);
				pstmt.setString(7, type);
				pstmt.executeUpdate();
				System.out.println(pstmt);
			}
		} else {
			System.out.println("NO ID PROVIDED");
			String insertGroup = "INSERT INTO groups (group_name, article_ids, admins, instructors, students, type) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(insertGroup)) {
				pstmt.setString(1, groupName);
				pstmt.setString(2, articleIds);
				pstmt.setString(3, admins);
				pstmt.setString(4, instructors);
				pstmt.setString(5, students);
				pstmt.setString(6, type);
				pstmt.executeUpdate();
				System.out.println(pstmt);
			}
		}
		System.out.println("Group created successfully.");
	}

	/**
	 * Adds or removes a user from a comma-separated column in the groups table.
	 * Ensures there is always at least one admin in the group.
	 * 
	 * @param groupId The ID of the group to update.
	 * @param column  The column to update (e.g., "admins", "instructors",
	 *                "students").
	 * @param userId  The user ID to add or remove.
	 * @param addUser True to add the user; false to remove the user.
	 * @throws SQLException If a database access error occurs.
	 */
	public void updateGroupUsers(String groupId, String column, String userId, boolean addUser) throws SQLException {
		// Fetch the current value of the column
		String query = "SELECT " + column + " FROM groups WHERE id = ?";
		String updatedColumnValue = "";
		boolean isAdminsColumn = column.equalsIgnoreCase("admins");

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, groupId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String currentValue = rs.getString(column);

				// Convert the current comma-separated value into a list
				List<String> users = new ArrayList<>();
				if (currentValue != null && !currentValue.trim().isEmpty()) {
					users = new ArrayList<>(Arrays.asList(currentValue.split(",")));
				}

				if (addUser) {
					// Add the user if not already present
					if (!users.contains(userId)) {
						users.add(userId);
					} else {
						System.out.println("User already exists in group");
					}
				} else {
					// Remove the user if present
					if (users.contains(userId)) {
						if (isAdminsColumn && users.size() == 1) {
							// Prevent removal if this is the last admin
							throw new SQLException("Cannot remove the last admin from the group.");
						}
						users.remove(userId);
					} else {
						System.out.println("User already exists in group");
					}

				}

				// Rebuild the comma-separated value
				updatedColumnValue = String.join(",", users);
			}
		}

		// Update the column with the new value
		String updateQuery = "UPDATE groups SET " + column + " = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
			pstmt.setString(1, updatedColumnValue);
			pstmt.setString(2, groupId);
			pstmt.executeUpdate();
		}

		System.out.println("User " + (addUser ? "added to" : "removed from") + " " + column + " successfully.");
	}

	/**
	 * Retrieves detailed information about a specific group by its ID.
	 *
	 * @param groupId The ID of the group to retrieve.
	 * @return A string containing the group's details, or a message if the group is
	 *         not found.
	 * @throws SQLException If a database access error occurs.
	 */
	public String getGroupInfo(String groupId) throws SQLException {
		ensureConnection(); // Ensure the connection is established

		String query = "SELECT * FROM groups WHERE id = ?";
		StringBuilder groupInfo = new StringBuilder();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, groupId);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					groupInfo.append("Group ID: ").append(groupId).append("\n").append("Group Name: ")
							.append(rs.getString("group_name")).append("\n").append("Article IDs: ")
							.append(rs.getString("article_ids")).append("\n").append("Admins: ")
							.append(rs.getString("admins")).append("\n").append("Instructors: ")
							.append(rs.getString("instructors")).append("\n").append("Students: ")
							.append(rs.getString("students")).append("\n").append("Type: ").append(rs.getString("type"))
							.append("\n").append("-------------------------------");
				} else {
					return "Group not found.";
				}
			}
		}

		return groupInfo.toString();
	}

	/**
	 * Lists all groups in the database, including their details.
	 * 
	 * @return A formatted string containing the details of all groups.
	 * @throws SQLException If a database access error occurs.
	 */
	public String listGroups() throws SQLException {
		ensureConnection(); // Ensure the connection is established

		StringBuilder groupsList = new StringBuilder();
		String query = "SELECT id, group_name, article_ids, admins, instructors, students, type FROM groups";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				int id = rs.getInt("id");
				String groupName = rs.getString("group_name");
				String articleIds = rs.getString("article_ids"); // Ensure this column name matches
				String admins = rs.getString("admins");
				String instructors = rs.getString("instructors");
				String students = rs.getString("students");
				String groupType = rs.getString("type");

				// Append group details to the list
				groupsList.append("Group ID: ").append(id).append("\n").append("Group Name: ").append(groupName)
						.append("\n").append("Article IDs: ").append(articleIds).append("\n").append("Admins: ")
						.append(admins).append("\n").append("Instructors: ").append(instructors).append("\n")
						.append("Students: ").append(students).append("\n").append("Type: ").append(groupType)
						.append("\n").append("-------------------------------\n");
			}
		}

		if (groupsList.length() == 0) {
			return "No groups found.";
		} else {
			return groupsList.toString();
		}
	}

	/**
	 * Deletes a group from the groups table.
	 * 
	 * @param groupId The ID of the group to delete.
	 * @throws SQLException If a database access error occurs.
	 */
	public void deleteGroup(String groupId) throws SQLException {
		String deleteGroup = "DELETE FROM groups WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(deleteGroup)) {
			pstmt.setString(1, groupId);
			pstmt.executeUpdate();
		}
		System.out.println("Group deleted successfully.");
	}

	public String[] groupsAccessibleToUser(String email) throws SQLException {
		ensureConnection();
		System.out.println("Getting Groups...");

		String queryIDs = "SELECT group_name FROM groups WHERE admins = ? OR instructors = ? OR students = ? OR type = ?";

		int id = Integer.parseInt(getUserIdFromEmail(email));

		List<String> groupList = new ArrayList<>();

		// Fetch group names
		try (PreparedStatement pstmt = connection.prepareStatement(queryIDs)) {
			pstmt.setInt(1, id);
			pstmt.setInt(2, id);
			pstmt.setInt(3, id);
			pstmt.setString(4, "general");
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) { // Iterate through all results
					groupList.add(rs.getString("group_name"));
				}
			}
		}

		// If no groups are found, return null
		if (groupList.isEmpty()) {
			System.out.println("No groups found for email: " + email);
			return null;
		}

		// Convert List<String> to String[]
		return groupList.toArray(new String[0]);
	}

	public int[] getArticlesInGroupList(String[] groupsProvided, String groupSpecifier) throws SQLException {
		ensureConnection();
		System.out.println("Checking Group Filter...");
		if (groupsProvided == null || groupsProvided.length == 0) {
			return null;
		}

		int articlesInDatabase = 0;

		String getCount = "SELECT COUNT(*) as count FROM articles";
		try (PreparedStatement pstmt = connection.prepareStatement(getCount)) {
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) { // Move to the first result
					articlesInDatabase = rs.getInt("count"); // Use the alias from the query
				}
			}
		}
		int[] returnArticles = new int[articlesInDatabase];

		String ids_string = "";

		String query = groupSpecifier.equals("*") ? "SELECT article_ids FROM groups"
				: "SELECT article_ids FROM groups WHERE group_name = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			if (!groupSpecifier.equals("*")) {
				pstmt.setString(1, groupSpecifier);
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					ids_string += rs.getString("article_ids") + ",";

				}
			}
		}

		if (ids_string.isBlank()) {
			System.out.println("No articles in group or it dosent exist!");
			return null;
		}

		// System.out.println("-------ID STRING: " + ids_string + "---------------");
		// TESTING
		String[] parts = ids_string.trim().split(","); // Split string by ',' delimiter

		// Convert String array to int array
		int i = 0;
		for (String part : parts) {

			if (!part.isBlank() && part.matches("\\d+")) {
				int id = Integer.parseInt(part.trim());

				boolean existsAlready = false;
				if (returnArticles != null) {
					for (int currID : returnArticles) {
						if (id == currID) {
							existsAlready = true;
						}
					}
				}

				if (existsAlready == false) {
					System.out.println("Article " + id + " in groups search!");
					returnArticles[i] = id;
					i++;
				}

			}
			if (part.isBlank()) {
				System.out.println("Article id is blank, skipping line");
			}

			else {
				System.out.println("Invalid article ID: " + part);
			}
		}

		return returnArticles;
	}

	public int[] articlesFilteredDifficulty(int[] articleIDs, String difficulty) throws SQLException {
		ensureConnection();
		System.out.println("Checking Difficulty Filter...");
		if (articleIDs == null || articleIDs.length == 0) {
			return null;
		}

		int[] returnArticles = new int[articleIDs.length];

		String query = "";
		if (difficulty.equals("*")) {
			query = "SELECT id FROM articles";
		} else {
			query = "SELECT id FROM articles WHERE difficulty = ?";
		}

		System.out.println("\tCHEKCPOINT : BEFORE SEARCH");
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			if (!difficulty.equals("*")) {
				pstmt.setString(1, difficulty.trim().toLowerCase());
			}
			System.out.println("\tCHEKCPOINT : BEFORE EXECUTION");
			try (ResultSet rs = pstmt.executeQuery()) {
				int index = 0;
				System.out.println("\tCHEKCPOINT : BEFORE WHILELOOP");
				System.out.println("\tCHEKCPOINT RS: " + rs);
				while (rs.next()) {
					int id = rs.getInt("id");
					System.out.println("\tCHEKCPOINT : GET ID");
					for (int i : articleIDs) {
						System.out.println("\tCHEKCPOINT : CHECKING ID AGAINST STORED VALS");
						if (i == id) {
							System.out.println("\tCHEKCPOINT : IDS BEING ADDED");
							System.out.println("Article " + id + " in difficulty search!");
							returnArticles[index] = rs.getInt("id");
							index++;
						}
					}

				}
				System.out.println("\tCHEKCPOINT : WHILELOOP ENDED");

			}
		}

		return returnArticles;
	}

	public String getArticlesFromList(int[] articleIDs) throws SQLException {
		ensureConnection(); // Ensure the connection is established
		if (articleIDs == null || articleIDs.length == 0) {
			return "No articles found.";
		}

		StringBuilder articlesDisplay = new StringBuilder();

		String query = "SELECT title, difficulty, authors FROM articles WHERE id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			int index = 0;
			while (index < articleIDs.length) {
				pstmt.setInt(1, articleIDs[index]);
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						int id = articleIDs[index];
						String title = rs.getString("title");
						String difficulty = rs.getString("difficulty"); // Retrieve difficulty
						String author = rs.getString("authors");

						// Append article details to the list
						articlesDisplay.append("ID: ").append(id).append("\n").append("Title: ").append(title)
								.append("\n").append("Difficulty: ").append(difficulty).append("\n") // Append
																										// difficulty
								.append("Author: ").append(author).append("\n")
								.append("-------------------------------\n");
					}
				}
				index++;
			}

		}

		if (articlesDisplay.isEmpty()) {
			return "No articles found.";
		} else {
			return articlesDisplay.toString();
		}

	}

	public String displayFullArticle(int id) throws SQLException {
		ensureConnection();

		String query = "SELECT * FROM articles WHERE id = ?";
		StringBuilder articleFull = new StringBuilder();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String title = rs.getString("title");
					String difficulty = rs.getString("difficulty"); // New difficulty field
					String authors = rs.getString("authors");
					String abstractText = rs.getString("abstract");
					String keywords = rs.getString("keywords");
					String body = rs.getString("body");
					String references = rs.getString("references");

					// Append article details to the list
					articleFull.append("ID: ").append(id).append("\n").append("Title: ").append(title).append("\n")
							.append("Difficulty: ").append(difficulty).append("\n") // Append difficulty
							.append("Author: ").append(authors).append("\n").append("Abstract: ").append(abstractText)
							.append("\n").append("Keywords: ").append(keywords).append("\n").append("Body: ")
							.append(body).append("\n").append("References: ").append(references).append("\n");
				} else {
					System.out.println("No article with this ID: " + id);
					return null;
				}

			}

		}

		if (articleFull.toString().isBlank()) {
			return "No articles found.";
		} else {
			return articleFull.toString();
		}

	}

	public int[] searchForArticles(String searchQuery, int[] idList) throws SQLException {
		ensureConnection();
		if (idList == null || idList.length == 0) {
			return null;
		} else if (searchQuery == null) {
			return idList;
		}

		// Build the query dynamically
		StringBuilder queryBuilder = new StringBuilder("SELECT * FROM articles WHERE id IN (");
		for (int i = 0; i < idList.length; i++) {
			queryBuilder.append("?");
			if (i < idList.length - 1) {
				queryBuilder.append(", ");
			}
		}
		queryBuilder.append(
				") AND (LOWER(keywords) LIKE ? OR LOWER(title) LIKE ? OR LOWER(authors) LIKE ? OR CAST(id AS CHAR) LIKE ?)");
		String query = queryBuilder.toString();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			// Set IDs
			for (int i = 0; i < idList.length; i++) {
				pstmt.setInt(i + 1, idList[i]);
			}

			// Set search query placeholders
			String searchPattern = "%" + searchQuery.toLowerCase() + "%";
			int baseIndex = idList.length + 1; // Start after the IDs
			pstmt.setString(baseIndex, searchPattern);
			pstmt.setString(baseIndex + 1, searchPattern);
			pstmt.setString(baseIndex + 2, searchPattern);
			pstmt.setString(baseIndex + 3, searchPattern);

			// Execute query
			try (ResultSet rs = pstmt.executeQuery()) {
				List<Integer> finalArticleIDsList = new ArrayList<>();
				while (rs.next()) {
					finalArticleIDsList.add(rs.getInt("id"));
				}
				return finalArticleIDsList.stream().mapToInt(Integer::intValue).toArray();
			}
		}
	}

	/**
	 * Retrieves a user's ID based on their email.
	 * 
	 * @param email The email address of the user.
	 * @return The user's ID, or null if the email is not found.
	 * @throws SQLException If a database error occurs.
	 */
	public String getUserIdFromEmail(String email) throws SQLException {
		ensureConnection();
		String query = "SELECT id FROM cse360users WHERE email = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getString("id");
				} else {
					System.out.println("No user found with email: " + email);
					return null;
				}
			}
		}
	}

	public String getPrefferedName(String email) throws SQLException {
		ensureConnection();
		// preferred_name
		String query = "SELECT preferred_name FROM cse360users WHERE email = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					String prefName = rs.getString("preferred_name");
					if (prefName == null) {
						return null;
					} 
					else if (prefName.isBlank()){
						return null;
					} 
					else { 
						return rs.getString("preferred_name");
					}
				} else {
					System.out.println("No user found with email: " + email);
					return null;
				}
			}
		}

	}

	public String getName(String email) throws SQLException {
		ensureConnection();
		// preferred_name
		String query = "SELECT first_name FROM cse360users WHERE email = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					if (getPrefferedName(email) == null) {
						return rs.getString("first_name");
					}
					return getPrefferedName(email);
				} else {
					System.out.println("No user found with email: " + email);
					return null;
				}
			}
		}

	}

	/**
	 * Backs up all groups to a specified file with encryption.
	 * 
	 * @param fileName The name of the file where the backup will be stored.
	 * @throws Exception If an error occurs during the backup process.
	 */
	public void backupGroups(String fileName) throws Exception {
		ensureConnection();
		String sql = "SELECT * FROM groups";
		ResultSet rs = statement.executeQuery(sql);

		List<String> groups = new ArrayList<>();
		while (rs.next()) {
			String id = rs.getString("id");
			String groupName = rs.getString("group_name");
			String articleIds = rs.getString("article_ids");
			String admins = rs.getString("admins");
			String instructors = rs.getString("instructors");
			String students = rs.getString("students");
			String type = rs.getString("type");
			groups.add(id + "|" + groupName + "|" + articleIds + "|" + admins + "|" + instructors + "|" + students + "|"
					+ type);
		}

		byte[] plainText = String.join("\n", groups).getBytes();
		byte[] initializationVector = EncryptionUtils.getInitializationVector("this-is-our-secret".toCharArray());
		byte[] encryptedData = encryptionHelper.encrypt(plainText, initializationVector);

		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			fos.write(initializationVector);
			fos.write(encryptedData);
		}
		System.out.println("Backup for groups created successfully.");
	}

	/**
	 * Restores all groups from a specified backup file.
	 * 
	 * @param fileName The name of the file from which the groups will be restored.
	 * @throws Exception If an error occurs during the restoration process.
	 */
	public void restoreGroups(String fileName) throws Exception {
		ensureConnection();
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
			String[] groups = new String(decryptedData).split("\n");

			// Clear existing groups
			statement.executeUpdate("DELETE FROM groups");

			for (String group : groups) {
				String[] fields = group.split("\\|");
				createGroup(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
			}
		}
		System.out.println("Restore for groups completed successfully.");
	}

	public String getGroupType(String groupId) throws SQLException {
		ensureConnection();
		String query = "SELECT type FROM groups WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, groupId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("type");
			}
			return null; // Group not found
		}
	}

	public boolean isUserAdminInGroup(String userId, String groupId) throws SQLException {
		ensureConnection();
		String query = "SELECT admins FROM groups WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, groupId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String admins = rs.getString("admins");
				return admins != null && Arrays.asList(admins.split(",")).contains(userId);
			}
			return false;
		}
	}

	/**
	 * 
	 * Retrieves and lists all students from the cse360users table.
	 * 
	 * 
	 * 
	 * @return A formatted string containing the details of all students.
	 * 
	 * @throws SQLException If a database access error occurs.
	 * 
	 */

	public String listStudents() throws SQLException {

		ensureConnection(); // Ensure the connection is established

		StringBuilder studentsList = new StringBuilder();

		String query = "SELECT id, first_name, email, roles FROM cse360users WHERE roles LIKE '%student%'";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {

				int id = rs.getInt("id");

				String name = rs.getString("first_name");

				String email = rs.getString("email");

				String roles = rs.getString("roles"); // Should be 'student' as filtered in the query

				// Append student details to the list

				studentsList.append("Student ID: ").append(id).append("\n")

						.append("First Name: ").append(name).append("\n")

						.append("Email: ").append(email).append("\n")

						.append("Role: ").append(roles).append("\n")

						.append("-------------------------------\n");

			}

		}

		if (studentsList.length() == 0) {

			return "No students found.";

		} else {

			return studentsList.toString();

		}

	}

	/**
	 * 
	 * Adds or removes an article from a comma-separated column in the groups table
	 * for articles.
	 * 
	 * 
	 * 
	 * @param groupId    The ID of the group to update.
	 * 
	 * @param articleId  The article ID to add or remove.
	 * 
	 * @param addArticle True to add the article; false to remove the article.
	 * 
	 * @throws SQLException If a database access error occurs.
	 * 
	 */

	public void updateGroupArticles(String groupId, String articleId, boolean addArticle) throws SQLException {

		// Fetch the current value of the article column

		String query = "SELECT article_ids FROM groups WHERE id = ?";

		String updatedColumnValue = "";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, groupId);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				String currentValue = rs.getString("article_ids");

				// Convert the current comma-separated value into a list

				List<String> articles = new ArrayList<>();

				if (currentValue != null && !currentValue.trim().isEmpty()) {

					articles = new ArrayList<>(Arrays.asList(currentValue.split(",")));

				}

				if (addArticle) {

					// Add the article if not already present

					if (!articles.contains(articleId)) {

						articles.add(articleId);

					} else {

						System.out.println("Article already exists in the list");

					}

				} else {

					// Remove the article if present

					if (articles.contains(articleId)) {

						articles.remove(articleId);

					} else {

						System.out.println("Article does not exist in the list");

					}

				}

				// Rebuild the comma-separated value

				updatedColumnValue = String.join(",", articles);

			}

		}

		// Update the article_ids column with the new value

		String updateQuery = "UPDATE groups SET article_ids = ? WHERE id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {

			pstmt.setString(1, updatedColumnValue);

			pstmt.setString(2, groupId);

			pstmt.executeUpdate();

		}

		System.out.println("Article " + (addArticle ? "added to" : "removed from") + " the group successfully.");

	}
	
	public boolean isInstructorInGroup(String groupId, String instructorId) throws SQLException {
        String query = "SELECT instructors FROM groups WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String instructors = rs.getString("instructors");
                if (instructors != null && !instructors.isEmpty()) {
                    String[] instructorList = instructors.split(",");
                    for (String instructor : instructorList) {
                        if (instructor.trim().equalsIgnoreCase(instructorId)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isInstructorAdminInGroup(String groupId, String instructorId) throws SQLException {
        String query = "SELECT admins FROM groups WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String admins = rs.getString("admins");
                if (admins != null && !admins.isEmpty()) {
                    String[] adminList = admins.split(",");
                    for (String admin : adminList) {
                        if (admin.trim().equalsIgnoreCase(instructorId)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void deleteAllGroups() {
        String deleteQuery = "DELETE FROM groups";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Number of groups deleted: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error occurred while deleting groups: " + e.getMessage());
        }
    }

}