package com.example.lms.db;

import com.example.lms.model.Book;
import com.example.lms.model.BookIssued;
import com.example.lms.model.Patron;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DBTest {

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        Connection initConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "Sandy_@98");
        try (Statement statement = initConnection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS test_library");
        }
        initConnection.close();

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_library", "root", "Sandy_@98");
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS book_detail (id VARCHAR(50), title VARCHAR(100), author VARCHAR(100), status VARCHAR(20))");
            statement.execute("CREATE TABLE IF NOT EXISTS member_detail (id VARCHAR(50), name VARCHAR(100), address VARCHAR(100), contact VARCHAR(20))");
            statement.execute("CREATE TABLE IF NOT EXISTS issue_table (issueId VARCHAR(50), date VARCHAR(50), patronId VARCHAR(50), bookId VARCHAR(50))");

            statement.execute("INSERT INTO book_detail (id, title, author, status) VALUES ('1', 'Test Book', 'Test Author', 'Available')");
            statement.execute("INSERT INTO member_detail (id, name, address, contact) VALUES ('1', 'Test Patron', '123 Test St', '1234567890')");
            statement.execute("INSERT INTO issue_table (issueId, date, patronId, bookId) VALUES ('1', '2024-07-18', '1', '1')");
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS book_detail");
            statement.execute("DROP TABLE IF EXISTS member_detail");
            statement.execute("DROP TABLE IF EXISTS issue_table");
        }
        connection.close();

        Connection initConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "Sandy_@98");
        try (Statement statement = initConnection.createStatement()) {
            statement.execute("DROP DATABASE IF EXISTS test_library");
        }
        initConnection.close();
    }

    @Test
    void testLoadBooks() {
        // Clear the books list before loading
        DB.books.clear();

        // Load books from the test database
        DB.loadBooks();

        // Assertions
        LinkedList<Book> books = DB.books;
        assertEquals(35, books.size());
        assertEquals("B002", books.getFirst().getId());
        assertEquals("very", books.getFirst().getTitle());
        assertEquals("wdsa", books.getFirst().getAuthor());
        assertEquals("Unavailable", books.getFirst().getStatus());
    }

    @Test
    void testLoadPatrons() {
        // Clear the patrons list before loading
        DB.patrons.clear();

        // Load patrons from the test database
        DB.loadPatrons();

        // Assertions
        LinkedList<Patron> patrons = DB.patrons;
        assertEquals(3, patrons.size());
        assertEquals("M001", patrons.getFirst().getId());
        assertEquals("kay", patrons.getFirst().getName());
        assertEquals("kay@gmail.com", patrons.getFirst().getAddress());
        assertEquals("0245123698", patrons.getFirst().getContact());
    }

    @Test
    void testLoadBooksIssued() {
        // Clear the bookIssued list before loading
        DB.bookIssued.clear();

        // Load issued books from the test database
        DB.loadBooksIssued();

        // Assertions
        LinkedList<BookIssued> bookIssued = DB.bookIssued;
        assertEquals(2, bookIssued.size());
        assertEquals("I001", bookIssued.getFirst().getIssueId());
        assertEquals("2024-07-12", bookIssued.getFirst().getDate());
        assertEquals("M002", bookIssued.getFirst().getPatronId());
        assertEquals("B003", bookIssued.getFirst().getBookId());
    }
}
