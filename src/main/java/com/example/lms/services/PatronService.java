package com.example.lms.services;

import com.example.lms.db.DbConnection;
import com.example.lms.model.Patron;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatronService {

    private final Connection connection;
    private final PreparedStatement newIdQuery;
    private final PreparedStatement addToTable;
    private final PreparedStatement updateQuery;
    private final PreparedStatement selectPatronID;
    private final PreparedStatement selectAll;

    public PatronService(Connection connection) throws SQLException, ClassNotFoundException {
        this.connection = connection;
        this.newIdQuery = this.connection.prepareStatement("SELECT id FROM member_detail");
        this.addToTable = this.connection.prepareStatement("INSERT INTO member_detail VALUES (?, ?, ?, ?)");
        this.updateQuery = this.connection.prepareStatement("UPDATE member_detail SET name=?, address=?, contact=? WHERE id=?");
        this.selectPatronID = this.connection.prepareStatement("SELECT * FROM member_detail WHERE id=?");
        this.selectAll = this.connection.prepareStatement("SELECT * FROM member_detail");
    }
    public PatronService() throws SQLException, ClassNotFoundException {
        DbConnection.getInstance();
        this.connection = DbConnection.getConnection();
        this.newIdQuery = this.connection.prepareStatement("SELECT id FROM member_detail");
        this.addToTable = this.connection.prepareStatement("INSERT INTO member_detail VALUES (?, ?, ?, ?)");
        this.updateQuery = this.connection.prepareStatement("UPDATE member_detail SET name=?, address=?, contact=? WHERE id=?");
        this.selectPatronID = this.connection.prepareStatement("SELECT * FROM member_detail WHERE id=?");
        this.selectAll = this.connection.prepareStatement("SELECT * FROM member_detail");
    }

    public List<Patron> getAllPatrons() throws SQLException {
        List<Patron> patrons = new ArrayList<>();
        try (ResultSet rst = selectAll.executeQuery()) {
            while (rst.next()) {
                patrons.add(new Patron(
                        rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)
                ));
            }
        }
        return patrons;
    }

    public Patron getPatronById(String id) throws SQLException {
        selectPatronID.setString(1, id);
        try (ResultSet rst = selectPatronID.executeQuery()) {
            if (rst.next()) {
                return new Patron(
                        rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)
                );
            }
        }
        return null;
    }

    public String generateNewId() throws SQLException {
        ResultSet rst = newIdQuery.executeQuery();
        String ids = null;
        int maxId = 0;
        while (rst.next()) {
            ids = rst.getString(1);
            int id = Integer.parseInt(ids.replace("M", ""));
            if (id > maxId) {
                maxId = id;
            }
        }
        maxId++;
        return String.format("M%03d", maxId);
    }

    public void addPatron(Patron patron) throws SQLException {
        addToTable.setString(1, patron.getId());
        addToTable.setString(2, patron.getName());
        addToTable.setString(3, patron.getAddress());
        addToTable.setString(4, patron.getContact());
        addToTable.executeUpdate();
    }

    public void updatePatron(Patron patron) throws SQLException {
        updateQuery.setString(1, patron.getName());
        updateQuery.setString(2, patron.getAddress());
        updateQuery.setString(3, patron.getContact());
        updateQuery.setString(4, patron.getId());
        updateQuery.executeUpdate();
    }

    public void deletePatron(String id) throws SQLException {
        try (PreparedStatement deleteQuery = connection.prepareStatement("DELETE FROM member_detail WHERE id=?")) {
            deleteQuery.setString(1, id);
            deleteQuery.executeUpdate();
        }
    }
}
