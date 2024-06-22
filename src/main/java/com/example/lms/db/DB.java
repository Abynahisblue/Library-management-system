package com.example.lms.db;

import com.example.lms.model.Book;
import com.example.lms.model.BookIssued;
import com.example.lms.model.BookReturn;
import com.example.lms.model.Patron;

import java.sql.*;
import java.util.LinkedList;

public class DB {
    public static LinkedList<Book> books = new LinkedList<>();
    public static LinkedList<BookIssued> bookIssued = new LinkedList<>();
    public static LinkedList<BookReturn> booksReturned = new LinkedList<>();
    public static LinkedList<Patron> patrons = new LinkedList<>();

    public static void loadBooks() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "Sandy_@98");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM book_detail")) {

            while (resultSet.next()) {
                books.add(new Book(resultSet.getString("id"), resultSet.getString("title"), resultSet.getString("author"), resultSet.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadPatrons() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "Sandy_@98");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM member_detail")) {

            while (resultSet.next()) {
                patrons.add(new Patron(resultSet.getString("id"), resultSet.getString("name"),resultSet.getString("address"), resultSet.getString("contact")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void loadBooksIssued() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "Sandy_@98");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM issue_table")) {

            while (resultSet.next()) {
                books.add(new Book(resultSet.getString("issueId"), resultSet.getString("date"), resultSet.getString("patronId"), resultSet.getString("bookId")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add similar methods for bookIssued and booksReturned if needed
}
