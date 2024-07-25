package com.example.lms.services;

import com.example.lms.db.DbConnection;
import com.example.lms.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookService {

    private static Connection connection;
    private static PreparedStatement selectAll;
    private final PreparedStatement selectByID;
    private static PreparedStatement newIdQuery;
    private static PreparedStatement insertBook;
    private static PreparedStatement updateBook;
    private static PreparedStatement deleteBook;

    private static final String SELECT_ALL_QUERY = "SELECT * FROM book_detail";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM book_detail WHERE id = ?";
    private static final String NEW_ID_QUERY = "SELECT id FROM book_detail";
    private static final String INSERT_BOOK_QUERY = "INSERT INTO book_detail VALUES (?, ?, ?, ?)";
    private static final String UPDATE_BOOK_QUERY = "UPDATE book_detail SET title = ?, author = ?, status = ? WHERE id = ?";
    private static final String DELETE_BOOK_QUERY = "DELETE FROM book_detail WHERE id = ?";
    private static final String CHECK_BOOK_STATUS_QUERY = "SELECT status FROM book_detail WHERE id = ?";




    public BookService(Connection connection) throws SQLException {
        System.out.println("Hi from book service");
        BookService.connection = connection;
        selectAll = connection.prepareStatement(SELECT_ALL_QUERY);
        updateBook = connection.prepareStatement(UPDATE_BOOK_QUERY);
        selectByID = connection.prepareStatement(SELECT_BY_ID_QUERY);
        insertBook = connection.prepareStatement(INSERT_BOOK_QUERY);
        newIdQuery = connection.prepareStatement(NEW_ID_QUERY);
        deleteBook = connection.prepareStatement(DELETE_BOOK_QUERY);
    }

    public BookService() throws SQLException {
        DbConnection.getInstance();
        System.out.println("Hi from book service");
        connection = DbConnection.getConnection();
        selectAll = connection.prepareStatement(SELECT_ALL_QUERY);
        updateBook = connection.prepareStatement(UPDATE_BOOK_QUERY);
        selectByID = connection.prepareStatement(SELECT_BY_ID_QUERY);
        insertBook = connection.prepareStatement(INSERT_BOOK_QUERY);
        newIdQuery = connection.prepareStatement(NEW_ID_QUERY);
        deleteBook = connection.prepareStatement(DELETE_BOOK_QUERY);
    }

    // Assuming getBooks returns a List<Book>
    public static List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        try (ResultSet rst = selectAll.executeQuery()) {
            while (rst.next()) {
                books.add(new Book(
                        rst.getString("id"),
                        rst.getString("title"),
                        rst.getString("author"),
                        rst.getString("status")
                ));
            }
        }
        return books;
    }

    // Ensure getBooks() method returns a mutable list
    private static List<Book> getBooks() {
        return new ArrayList<>(/* collection of books */);
    }

    public static String generateNewId() throws SQLException {
        int maxId = 0;
        try (ResultSet rst = newIdQuery.executeQuery()) {
            while (rst.next()) {
                int id = Integer.parseInt(rst.getString("id").replace("B", ""));
                if (id > maxId) {
                    maxId = id;
                }
            }
        }
        maxId++;
        return String.format("B%03d", maxId);
    }

    public  String addBook(Book book) throws SQLException {
        insertBook.setString(1, book.getId());
        insertBook.setString(2, book.getTitle());
        insertBook.setString(3, book.getAuthor());
        insertBook.setString(4, book.getStatus());
        return executeUpdate(insertBook, "Book added successfully.");
    }
    public  String updateBook(Book book) throws SQLException {
        updateBook.setString(1, book.getId());
        updateBook.setString(2, book.getTitle());
        updateBook.setString(3, book.getAuthor());
        updateBook.setString(4, book.getStatus());
        return executeUpdate(updateBook, "Book updated successfully.");
    }
    public   int deleteBook(String selectedBookId) throws SQLException {
        deleteBook.setString(1, selectedBookId);
        return deleteBook.executeUpdate();
    }

    public  boolean isBookAvailable(String bookId) throws SQLException {
        try (PreparedStatement pstm = connection.prepareStatement(CHECK_BOOK_STATUS_QUERY)) {
            pstm.setString(1, bookId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    return "Available".equalsIgnoreCase(status);
                } else {
                    throw new SQLException("Book ID not found: " + bookId);
                }
            }
        }
    }
    private String executeUpdate(PreparedStatement preparedStatement, String successMessage) throws SQLException {
        int affectedRows = preparedStatement.executeUpdate();
        System.out.println(affectedRows);
        if (affectedRows > 0) {
            System.out.println(successMessage);
            return successMessage;
        } else {
            System.out.println("No rows affected.");
        }
        return successMessage;
    }

}
