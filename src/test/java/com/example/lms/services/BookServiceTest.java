package com.example.lms.services;

import com.example.lms.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {

    private BookService bookService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Set up H2 in-memory database
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        createTables();
        bookService = new BookService(connection);
    }

    private void createTables() throws SQLException {
        try (PreparedStatement dropStmt = connection.prepareStatement("DROP TABLE IF EXISTS book_detail")) {
            dropStmt.execute();
        }
        try (PreparedStatement createStmt = connection.prepareStatement(
                "CREATE TABLE book_detail (id VARCHAR(255) PRIMARY KEY, title VARCHAR(255), author VARCHAR(255), status VARCHAR(255))")) {
            createStmt.execute();
        }
    }


    @Test
    void testGetAllBooks() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO book_detail (id, title, author, status) VALUES ('B001', 'Author 1','Book Title 1', 'Available')")) {
            stmt.execute();
        }
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO book_detail (id, title, author, status) VALUES ('B002', 'Book Title 2', 'Author 2', 'Unavailable')")) {
            stmt.execute();
        }

        List<Book> books = BookService.getAllBooks();
        assertEquals(2, books.size());
        assertEquals("B001", books.get(0).getId());
        assertEquals("Book Title 1", books.get(0).getTitle());
        assertEquals("Author 1", books.get(0).getAuthor());
        assertEquals("Available", books.get(0).getStatus());
    }

    @Test
    void testGenerateNewId() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO book_detail (id, title, author, status) VALUES ('B001', 'Book Title 1', 'Author 1', 'Available')")) {
            stmt.execute();
        }
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO book_detail (id, title, author, status) VALUES ('B002', 'Book Title 2', 'Author 2', 'Unavailable')")) {
            stmt.execute();
        }

        String newId = BookService.generateNewId();
        assertEquals("B003", newId);
    }

    @Test
    void testAddBook() throws SQLException {
        Book book = new Book("B003", "Author 3", "Book Title 3", "Available");
        String result = bookService.addBook(book);
        assertEquals("Book added successfully.", result);

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM book_detail WHERE id = 'B003'")) {
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Book Title 3", rs.getString("title"));
            assertEquals("Author 3", rs.getString("author"));
            assertEquals("Available", rs.getString("status"));
        }
    }

    @Test
    void testUpdateBook() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO book_detail (id, title, author, status) VALUES ('B004', 'Old Title', 'Old Author', 'Unavailable')")) {
            stmt.execute();
        }

        Book book = new Book("B004", "New Title", "New Author", "Available");
        String result = bookService.updateBook(book);
        assertEquals("Book updated successfully.", result);

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM book_detail WHERE id = 'B004'")) {
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Old Title", rs.getString("title"));
            assertEquals("Old Author", rs.getString("author"));
            assertEquals("Unavailable", rs.getString("status"));
        }
    }

    @Test
    void testDeleteBook() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO book_detail (id, title, author, status) VALUES ('B005', 'Book Title 5', 'Author 5', 'Available')")) {
            stmt.execute();
        }

        int rowsAffected = bookService.deleteBook("B005");
        assertEquals(1, rowsAffected);

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM book_detail WHERE id = 'B005'")) {
            ResultSet rs = stmt.executeQuery();
            assertFalse(rs.next());
        }
    }

    @Test
    void testIsBookAvailable() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO book_detail (id, title, author, status) VALUES ('B006', 'Book Title 6', 'Author 6', 'Available')")) {
            stmt.execute();
        }

        boolean available = bookService.isBookAvailable("B006");
        assertTrue(available);
    }

    @Test
    void testIsBookAvailable_NotAvailable() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO book_detail (id, title, author, status) VALUES ('B007', 'Book Title 7', 'Author 7', 'Unavailable')")) {
            stmt.execute();
        }

        boolean available = bookService.isBookAvailable("B007");
        assertFalse(available);
    }
}
