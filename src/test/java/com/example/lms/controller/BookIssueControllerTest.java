package com.example.lms.controller;

import com.example.lms.db.DbConnection;
import com.example.lms.model.BookIssued;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class BookIssueControllerTest extends ApplicationTest {

    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/library_test?createDatabaseIfNotExist=true&allowMultiQueries=true";
    private static final String TEST_DB_USER = "root";
    private static final String TEST_DB_PASSWORD = "Sandy_@98";

    private Connection connection;
    private BookIssueController controller;

    @BeforeAll
    public static void setupTestDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD)) {
            String sql = "CREATE DATABASE IF NOT EXISTS library_test";
            connection.prepareStatement(sql).execute();
        }
    }

    @BeforeEach
    public void setup() throws Exception {
        connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASSWORD);

        // Ensure DbConnection is set up correctly
        DbConnection dbConnection = DbConnection.getInstance();
        dbConnection.setConnection(connection);

        // Create required tables
        String createTablesSQL = "CREATE TABLE IF NOT EXISTS book_detail (" +
                "id VARCHAR(50) PRIMARY KEY, " +
                "title VARCHAR(100), " +
                "author VARCHAR(100), " +
                "status VARCHAR(20))";
        try (PreparedStatement statement = connection.prepareStatement(createTablesSQL)) {
            statement.execute();
        }

        String createIssueTableSQL = "CREATE TABLE IF NOT EXISTS issue_table (" +
                "issueId VARCHAR(50) PRIMARY KEY, " +
                "date DATE, " +
                "patronId VARCHAR(50), " +
                "bookId VARCHAR(50), " +
                "FOREIGN KEY (bookId) REFERENCES book_detail(id))";
        try (PreparedStatement statement = connection.prepareStatement(createIssueTableSQL)) {
            statement.execute();
        }

        // Clear existing data
        try (PreparedStatement clearTable = connection.prepareStatement("DELETE FROM book_detail; DELETE FROM issue_table")) {
            clearTable.execute();
        }

        // Add initial data
        String insertBookSQL = "INSERT INTO book_detail (id, title, author, status) VALUES ('B001', 'Existing Book', 'Existing Author', 'Available')";
        try (PreparedStatement statement = connection.prepareStatement(insertBookSQL)) {
            statement.execute();
        }

        // Load the FXML and initialize the controller
        FxRobot robot = new FxRobot();
        robot.interact(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/lms/BookIssued.fxml"));
                Parent root = loader.load();
                controller = loader.getController();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @AfterEach
    public void tearDown() {
        try {
            if (connection != null && !connection.isClosed()) {
                // Close the connection
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAddBookIssue() throws SQLException {
        FxRobot robot = new FxRobot();

        // Open the new action dialog
        robot.clickOn("#new_action");

        // Wait for the ComboBox to be populated and select the patron
        robot.interact(() -> {
            try {
                // Ensure the patron list is populated
                robot.lookup("#mem_is_id").queryComboBox().getItems(); // Ensure the combo box items are loaded
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Check that the "Patron1" is available and select it
        var patronComboBox = robot.lookup("#mem_is_id").queryComboBox();
        assertNotNull(patronComboBox, "Patron ComboBox should be present");
        assertTrue(patronComboBox.getItems().contains("M001"), "M001 should be in the ComboBox");

        robot.clickOn("#mem_is_id").clickOn("M001");

        // Select the book ID
        robot.clickOn("#book_id").clickOn("B002");

        // Open the DatePicker
        robot.clickOn("#txt_isu_date");

        // Wait for the DatePicker popup to be visible
        robot.sleep(500); // Adjust sleep time if necessary

        // Lookup and click on the desired date (e.g., 2024-07-23)
        var dateCells = robot.lookup(".calendar-day").queryAll();
        assertFalse(dateCells.isEmpty(), "No calendar days found in the DatePicker");

        dateCells.stream()
                .filter(dayNode -> {
                    String text = dayNode.getAccessibleText();
                    return "23".equals(text); // Adjust based on actual text content
                })
                .findFirst()
                .ifPresent(robot::clickOn);

        // Click on add action
        robot.clickOn("#add_Action");

        // Validate the book issue was added
        try (PreparedStatement selectIssue = connection.prepareStatement("SELECT * FROM issue_table WHERE issueId = 'I001'")) {
            var resultSet = selectIssue.executeQuery();
            assertTrue(resultSet.next(), "Book issue should be added to the database");
            assertEquals("I001", resultSet.getString("issueId"));
        }
    }


    @Test
    void testDeleteBookIssue() throws SQLException {
        FxRobot robot = new FxRobot();

        // Add an issue to delete
        String insertIssueSQL = "INSERT INTO issue_table (issueId, date, patronId, bookId) VALUES ('I001', '2024-07-01', 'Patron1', 'B001')";
        try (PreparedStatement statement = connection.prepareStatement(insertIssueSQL)) {
            statement.execute();
        }

        // Select the first row in the TableView
        robot.clickOn("#bk_issue_tbl");
        robot.type(KeyCode.DOWN);

        // Delete the book issue
        robot.clickOn("#btn_dlt");

        // Validate the book issue was deleted
        try (PreparedStatement selectIssue = connection.prepareStatement("SELECT * FROM issue_table WHERE issueId = 'I001'")) {
            var resultSet = selectIssue.executeQuery();
            assertFalse(resultSet.next(), "Book issue should be deleted from the database");
        }
    }

    // Add more tests as needed
}
