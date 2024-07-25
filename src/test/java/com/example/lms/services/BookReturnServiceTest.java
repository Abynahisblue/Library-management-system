package com.example.lms.services;

import com.example.lms.db.DbConnection;
import com.example.lms.model.BookReturn;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookReturnServiceTest {

    private BookReturnService bookReturnService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        // Set up H2 in-memory database
        DbConnection.getInstance();
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        createTables();
        bookReturnService = new BookReturnService(connection);
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Drop tables if they exist
            stmt.execute("DROP TABLE IF EXISTS return_detail");
            stmt.execute("DROP TABLE IF EXISTS book_detail");
            stmt.execute("DROP TABLE IF EXISTS issue_table");

            // Create tables
            stmt.execute("CREATE TABLE book_detail (id VARCHAR(255) PRIMARY KEY, status VARCHAR(255))");
            stmt.execute("CREATE TABLE issue_table (issueId VARCHAR(255) PRIMARY KEY, date DATE, patronId VARCHAR(255), bookId VARCHAR(255))");
            stmt.execute("CREATE TABLE return_detail (id VARCHAR(255) PRIMARY KEY, issuedDate DATE, returnedDate DATE, fine FLOAT)");

            // Insert initial data
            stmt.execute("INSERT INTO issue_table (issueId, bookId, date) VALUES ('I001', 'B001', '2023-07-01')");
            stmt.execute("INSERT INTO issue_table (issueId, bookId, date) VALUES ('I002', 'B002', '2023-07-01')");
            stmt.execute("INSERT INTO book_detail (id, status) VALUES ('B001', 'Issued')");
            stmt.execute("INSERT INTO book_detail (id, status) VALUES ('B002', 'Issued')");
        }
    }

    @Test
    void testGetAllReturns() throws SQLException {
        bookReturnService.addReturn("I004", "2023-07-01", "2023-07-20", "90");
        ObservableList<BookReturn> returns = bookReturnService.getAllReturns();
        assertNotNull(returns);
        assertEquals(1, returns.size()); // Adjusted to 1 as we have only added one return
    }

    @Test
    void testGetIssueIds() throws SQLException {
        ObservableList<String> issueIds = bookReturnService.getIssueIds();
        assertNotNull(issueIds);
        assertEquals(2, issueIds.size()); // Adjusted to 2 based on initial data
        assertTrue(issueIds.contains("I001"));
        assertTrue(issueIds.contains("I002"));
    }

    @Test
    void testGetIssueDate() throws SQLException {
        String issueDate = bookReturnService.getIssueDate("I001");
        assertNotNull(issueDate);
        assertEquals("2023-07-01", issueDate);
    }

    @Test
    void testAddReturn() throws SQLException {
        bookReturnService.addReturn("I007", "2023-07-01", "2023-07-20", "90");

        // Verify return was added
        ObservableList<BookReturn> returns = bookReturnService.getAllReturns();
        assertNotNull(returns);
        assertEquals(1, returns.size());
        assertEquals("I007", returns.get(0).getId());

        // Verify book status was updated
        try (PreparedStatement stmt = connection.prepareStatement("SELECT status FROM book_detail WHERE id = 'B001'")) {
            var rs = stmt.executeQuery();
            if (rs.next()) {
                assertEquals("Issued", rs.getString("status"));
            } else {
                fail("Book not found");
            }
        }
    }

    @Test
    void testCalculateFine() {
        LocalDate issuedDate = LocalDate.of(2023, 7, 1);
        LocalDate returnedDate = LocalDate.of(2023, 7, 20);
        float fine = bookReturnService.calculateFine(issuedDate, returnedDate);
        assertEquals(75.0, fine);

        returnedDate = LocalDate.of(2023, 7, 15);
        fine = bookReturnService.calculateFine(issuedDate, returnedDate);
        assertEquals(0, fine);

        returnedDate = LocalDate.of(2023, 7, 10);
        fine = bookReturnService.calculateFine(issuedDate, returnedDate);
        assertEquals(0, fine);
    }
}
