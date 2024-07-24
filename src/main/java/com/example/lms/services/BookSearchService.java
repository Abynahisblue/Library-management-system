package com.example.lms.services;


import com.example.lms.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class BookSearchService {
    private final Connection connection;

    public BookSearchService() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "Sandy_@98");
    }

    public ObservableList<Book> getAllBooks() throws SQLException {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT * from book_detail";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        while (rst.next()) {
            books.add(new Book(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
        }
        return books;
    }

    public ObservableList<Book> searchBooks(String searchText) throws SQLException {
        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql = "SELECT * FROM book_detail WHERE id LIKE ? OR title LIKE ? OR author LIKE ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        String like = "%" + searchText + "%";
        pstm.setString(1, like);
        pstm.setString(2, like);
        pstm.setString(3, like);
        ResultSet rst = pstm.executeQuery();
        while (rst.next()) {
            books.add(new Book(rst.getString(1), rst.getString(2), rst.getString(3), rst.getString(4)));
        }
        return books;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

