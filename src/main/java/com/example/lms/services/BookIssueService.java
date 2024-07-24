package com.example.lms.services;


import com.example.lms.db.DbConnection;
import com.example.lms.model.BookIssued;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookIssueService {
    private final Connection connection;
    private final PreparedStatement selectAll;
    private final PreparedStatement insertStatement;
    private final PreparedStatement updateBookStatus;
    private final PreparedStatement selectBookStatus;
    private final PreparedStatement selectBookIdFromIssue;
    private final PreparedStatement deleteStatement;

    public BookIssueService() throws SQLException, ClassNotFoundException {
        DbConnection.getInstance();
        this.connection = DbConnection.getConnection();
        this.selectAll = connection.prepareStatement("SELECT * FROM issue_table");
        this.insertStatement = connection.prepareStatement("INSERT INTO issue_table (issueId, date, patronId, bookId) VALUES (?, ?, ?, ?)");
        this.updateBookStatus = connection.prepareStatement("UPDATE book_detail SET status = 'Unavailable' WHERE id = ?");
        this.selectBookStatus = connection.prepareStatement("SELECT status FROM book_detail WHERE id = ?");
        this.selectBookIdFromIssue = connection.prepareStatement("SELECT bookId FROM issue_table WHERE issueId = ?");
        this.deleteStatement = connection.prepareStatement("DELETE FROM issue_table WHERE issueId = ?");
    }

    public List<BookIssued> getAllBooksIssued() throws SQLException {
        List<BookIssued> booksIssued = new ArrayList<>();
        try (ResultSet rst = selectAll.executeQuery()) {
            while (rst.next()) {
                booksIssued.add(new BookIssued(
                        rst.getString("issueId"),
                        rst.getString("date"),
                        rst.getString("patronId"),
                        rst.getString("bookId")
                ));
            }
        }
        return booksIssued;
    }

    public void addBookIssue(BookIssued bookIssued) throws SQLException {
        insertStatement.setString(1, bookIssued.getIssueId());
        insertStatement.setString(2, bookIssued.getDate());
        insertStatement.setString(3, bookIssued.getPatronId());
        insertStatement.setString(4, bookIssued.getBookId());
        insertStatement.executeUpdate();
    }

    public void updateBookStatusToUnavailable(String issueID) throws SQLException {
        String bookId = getBookIdFromIssue(issueID);
        updateBookStatus.setString(1, bookId);
        updateBookStatus.executeUpdate();
    }

    private String getBookIdFromIssue(String issueID) throws SQLException {
        selectBookIdFromIssue.setString(1, issueID);
        try (ResultSet rs = selectBookIdFromIssue.executeQuery()) {
            if (rs.next()) {
                return rs.getString("bookId");
            } else {
                throw new SQLException("Issue ID not found: " + issueID);
            }
        }
    }

    public boolean isBookAvailable(String bookId) throws SQLException {
        selectBookStatus.setString(1, bookId);
        try (ResultSet rs = selectBookStatus.executeQuery()) {
            if (rs.next()) {
                String status = rs.getString("status");
                return "Available".equalsIgnoreCase(status);
            } else {
                throw new SQLException("Book ID not found: " + bookId);
            }
        }
    }

    public void deleteBookIssue(String issueId) throws SQLException {
        deleteStatement.setString(1, issueId);
        deleteStatement.executeUpdate();
    }
}
