package com.example.lms.db;


import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class DbConnectionTest {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_test?createDatabaseIfNotExist=true&allowMultiQueries=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sandy_@98";

    @BeforeAll
    public static void setupTestDatabase() {
        // Setup the database connection for testing
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DROP DATABASE IF EXISTS library_test; CREATE DATABASE library_test;";
            connection.prepareStatement(sql).execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup test database");
        }
    }

    @BeforeEach
    public void initializeDatabase() {
        // Initialize the database by creating the instance
        DbConnection dbConnection = DbConnection.getInstance();
        dbConnection.getConnection();
    }

    @Test
    void testConnectionEstablished() {
        // Verify the connection is established
        DbConnection dbConnection = DbConnection.getInstance();
        assertNotNull(dbConnection.getConnection(), "Connection should not be null");
    }

    @Test
    void testTablesCreated() {
        // Verify the tables are created
        DbConnection dbConnection = DbConnection.getInstance();
        try (Connection connection = dbConnection.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Verify member_detail table
            try (ResultSet rs = metaData.getTables(null, null, "member_detail", null)) {
                assertTrue(rs.next(), "member_detail table should exist");
            }

            // Verify book_detail table
            try (ResultSet rs = metaData.getTables(null, null, "book_detail", null)) {
                assertTrue(rs.next(), "book_detail table should exist");
            }

            // Verify issue_table table
            try (ResultSet rs = metaData.getTables(null, null, "issue_table", null)) {
                assertTrue(rs.next(), "issue_table table should exist");
            }

            // Verify return_detail table
            try (ResultSet rs = metaData.getTables(null, null, "return_detail", null)) {
                assertTrue(rs.next(), "return_detail table should exist");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException occurred during table verification");
        }
    }

    @AfterEach
    public void cleanupDatabase() {
        // Clean up the test database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DROP DATABASE IF EXISTS library_test;";
            connection.prepareStatement(sql).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
