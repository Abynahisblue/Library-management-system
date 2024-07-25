package com.example.lms.services;

import com.example.lms.model.BookIssued;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookIssueServiceTest {

    private BookIssueService bookIssueService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        // Set up H2 in-memory database
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        createTables();
        bookIssueService = new BookIssueService(connection);
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Drop tables if they exist
            stmt.execute("DROP TABLE IF EXISTS book_detail");
            stmt.execute("DROP TABLE IF EXISTS issue_table");

            // Create tables
            stmt.execute("CREATE TABLE book_detail (id VARCHAR(255) PRIMARY KEY, status VARCHAR(255))");
            stmt.execute("CREATE TABLE issue_table (issueId VARCHAR(255) PRIMARY KEY, date DATE, patronId VARCHAR(255), bookId VARCHAR(255))");
        }
    }

    @Test
    void testGetAllBooksIssued() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO issue_table (issueId, date, patronId, bookId) VALUES ('I001', '2024-07-22', 'M001', 'B001')");
            stmt.execute("INSERT INTO issue_table (issueId, date, patronId, bookId) VALUES ('I002', '2023-01-02', 'P2', 'B2')");
        }

        List<BookIssued> booksIssued = bookIssueService.getAllBooksIssued();
        assertEquals(2, booksIssued.size());
        assertEquals("I001", booksIssued.get(0).getIssueId());
        assertEquals("I002", booksIssued.get(1).getIssueId());
    }

    @Test
    void testAddBookIssue() throws SQLException {
        // Ensure the book exists in the book_detail table
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO book_detail (id, status) VALUES ('B002', 'Available')");
        }

        // Create the BookIssued object
        BookIssued bookIssued = new BookIssued("I006", "2024-07-24", "M002", "B002");

        // Call the method under test
        bookIssueService.addBookIssue(bookIssued);

        // Verify that the book issue is added
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM issue_table WHERE issueId = ?")) {
            pstmt.setString(1, "I006");
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                assertEquals(1, rs.getInt(1));
            }
        }
    }

    @Test
    void testUpdateBookStatusToUnavailable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO book_detail (id, status) VALUES ('B014', 'Available')");
            stmt.execute("INSERT INTO issue_table (issueId, bookId) VALUES ('I003', 'B014')");
        }

        bookIssueService.updateBookStatusToUnavailable("I003");

        try (PreparedStatement pstmt = connection.prepareStatement("SELECT status FROM book_detail WHERE id = ?")) {
            pstmt.setString(1, "B014");
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                assertEquals("Unavailable", rs.getString("status"));
            }
        }
    }

    @Test
    void testIsBookAvailable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO book_detail (id, status) VALUES ('B017', 'Available')");
        }

        boolean available = bookIssueService.isBookAvailable("B017");
        assertTrue(available);
    }

    @Test
    void testDeleteBookIssue() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO issue_table (issueId, bookId) VALUES ('I001', 'B001')");
        }

        bookIssueService.deleteBookIssue("I001");

        try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM issue_table WHERE issueId = ?")) {
            pstmt.setString(1, "I001");
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                assertEquals(0, rs.getInt(1));
            }
        }
    }
}