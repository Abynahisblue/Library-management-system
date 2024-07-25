package com.example.lms.services;

import com.example.lms.model.Patron;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatronServiceTest {

    private PatronService patronService;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        // Set up H2 in-memory database
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        createTables();
        patronService = new PatronService(connection);
    }

    private void createTables() throws SQLException {
        try (PreparedStatement dropStmt = connection.prepareStatement("DROP TABLE IF EXISTS member_detail")) {
            dropStmt.execute();
        }
        try (PreparedStatement createStmt = connection.prepareStatement(
                "CREATE TABLE member_detail (id VARCHAR(255) PRIMARY KEY, name VARCHAR(255), address VARCHAR(255), contact VARCHAR(255))")) {
            createStmt.execute();
        }
    }

    @Test
    void testGetAllPatrons() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO member_detail (id, name, address, contact) VALUES ('M001', 'John Doe', '123 Main St', '555-1234')")) {
            stmt.execute();
        }
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO member_detail (id, name, address, contact) VALUES ('M002', 'Jane Smith', '456 Oak Ave', '555-5678')")) {
            stmt.execute();
        }

        List<Patron> patrons = patronService.getAllPatrons();
        assertEquals(2, patrons.size());
        assertEquals("M001", patrons.get(0).getId());
        assertEquals("M002", patrons.get(1).getId());
    }

    @Test
    void testGetPatronById() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO member_detail (id, name, address, contact) VALUES ('M003', 'Alice Johnson', '789 Pine Rd', '555-8765')")) {
            stmt.execute();
        }

        Patron patron = patronService.getPatronById("M003");
        assertNotNull(patron);
        assertEquals("M003", patron.getId());
        assertEquals("Alice Johnson", patron.getName());
        assertEquals("789 Pine Rd", patron.getAddress());
        assertEquals("555-8765", patron.getContact());
    }

    @Test
    void testGenerateNewId() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO member_detail (id, name, address, contact) VALUES ('M004', 'Bob Brown', '321 Elm St', '555-4321')")) {
            stmt.execute();
        }

        String newId = patronService.generateNewId();
        assertEquals("M005", newId);
    }

    @Test
    void testAddPatron() throws SQLException {
        Patron patron = new Patron("M006", "Carol White", "654 Maple Dr", "555-0987");
        patronService.addPatron(patron);

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM member_detail WHERE id = 'M006'")) {
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Carol White", rs.getString("name"));
            assertEquals("654 Maple Dr", rs.getString("address"));
            assertEquals("555-0987", rs.getString("contact"));
        }
    }

    @Test
    void testUpdatePatron() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO member_detail (id, name, address, contact) VALUES ('M007', 'Dave Green', '987 Birch Ln', '555-6543')")) {
            stmt.execute();
        }

        Patron patron = new Patron("M007", "David Green", "987 Birch Lane", "555-6544");
        patronService.updatePatron(patron);

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM member_detail WHERE id = 'M007'")) {
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("David Green", rs.getString("name"));
            assertEquals("987 Birch Lane", rs.getString("address"));
            assertEquals("555-6544", rs.getString("contact"));
        }
    }

    @Test
    void testDeletePatron() throws SQLException {
        // Insert test data
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO member_detail (id, name, address, contact) VALUES ('M008', 'Eve Black', '123 Cedar St', '555-8765')")) {
            stmt.execute();
        }

        patronService.deletePatron("M008");

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM member_detail WHERE id = 'M008'")) {
            ResultSet rs = stmt.executeQuery();
            assertFalse(rs.next());
        }
    }
}
