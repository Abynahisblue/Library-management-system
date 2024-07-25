package com.example.lms.services;


import com.example.lms.db.DbConnection;
import com.example.lms.model.BookReturn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class BookReturnService {
    private final Connection connection;

    public BookReturnService(Connection connection) throws SQLException {
        this.connection = connection;
    }
    public BookReturnService() throws SQLException {
        this.connection = DbConnection.getInstance().getConnection();
    }


    public ObservableList<BookReturn> getAllReturns() throws SQLException {
        ObservableList<BookReturn> returns = FXCollections.observableArrayList();
        String sql = "SELECT * FROM return_detail";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        while (rst.next()) {
            float fine = rst.getFloat("fine");
            returns.add(new BookReturn(rst.getString("id"), rst.getString("issuedDate"), rst.getString("returnedDate"), fine));
        }
        return returns;
    }

    public ObservableList<String> getIssueIds() throws SQLException {
        ObservableList<String> issueIds = FXCollections.observableArrayList();
        String sql = "SELECT issueId FROM issue_table";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rst = pstm.executeQuery();
        while (rst.next()) {
            issueIds.add(rst.getString("issueId"));
        }
        return issueIds;
    }

    public String getIssueDate(String issueId) throws SQLException {
        String sql = "SELECT date FROM issue_table WHERE issueId = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, issueId);
        ResultSet rst = pstm.executeQuery();
        if (rst.next()) {
            return rst.getString("date");
        }
        return null;
    }

    public void addReturn(String issueID, String issuedDate, String returnedDate, String fine) throws SQLException {
        String sql = "INSERT INTO return_detail (id, issuedDate, returnedDate, fine) VALUES (?, ?, ?, ?)";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, issueID);
        pstm.setString(2, issuedDate);
        pstm.setString(3, returnedDate);
        pstm.setString(4, fine);
        pstm.executeUpdate();

        updateBookStatus(issueID);
    }

    private void updateBookStatus(String issueID) throws SQLException {
        String bookId = getBookIdFromIssue(issueID);
        String sql = "UPDATE book_detail SET status = 'Available' WHERE id = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, bookId);
        pstm.executeUpdate();
    }

    private String getBookIdFromIssue(String issueID) throws SQLException {
        String sql = "SELECT bookId FROM issue_table WHERE issueId = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, issueID);
        ResultSet rst = pstm.executeQuery();
        if (rst.next()) {
            return rst.getString("bookId");
        }
        return null;
    }

    public float calculateFine(LocalDate issuedDate, LocalDate returnedDate) {
        long daysBetween = TimeUnit.DAYS.convert(
                Date.valueOf(returnedDate).getTime() - Date.valueOf(issuedDate).getTime(),
                TimeUnit.MILLISECONDS
        );

        return daysBetween > 14 ? (daysBetween - 14) * 15 : 0;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

