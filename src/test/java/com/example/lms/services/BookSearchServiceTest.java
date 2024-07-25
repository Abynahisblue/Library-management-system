package com.example.lms.services;

import com.example.lms.model.Book;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookSearchServiceTest {

    private BookSearchService bookSearchService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Set up H2 in-memory database
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        createTables();
        bookSearchService = new BookSearchService(connection);
    }

    private void createTables() throws SQLException {
        try (PreparedStatement dropStmt = connection.prepareStatement("DROP TABLE IF EXISTS book_detail")) {
            dropStmt.execute();
        }
        try (PreparedStatement createStmt = connection.prepareStatement(
                "CREATE TABLE book_detail (id VARCHAR(255) PRIMARY KEY, title VARCHAR(255), author VARCHAR(255), status VARCHAR(255))")) {
            createStmt.execute();
        }
        try (PreparedStatement insertStmt = connection.prepareStatement(
                "INSERT INTO book_detail (id, title, author, status) VALUES " +
                        "('B001', 'Title1', 'Author1', 'Available')," +
                        "('B002', 'Title2', 'Author2', 'Available')," +
                        "('B003', 'Another Title', 'Another Author', 'Unavailable')")) {
            insertStmt.execute();
        }
    }

    @Test
    void testGetAllBooks() throws SQLException {
        ObservableList<Book> books = bookSearchService.getAllBooks();
        assertNotNull(books);
        assertEquals(3, books.size());
    }

    @Test
    void testSearchBooks() throws SQLException {
        ObservableList<Book> books = bookSearchService.searchBooks("Title");
        assertNotNull(books);
        assertEquals(3, books.size());

        books = bookSearchService.searchBooks("Another");
        assertNotNull(books);
        assertEquals(1, books.size());

        books = bookSearchService.searchBooks("Nonexistent");
        assertNotNull(books);
        assertEquals(0, books.size());
    }
}
