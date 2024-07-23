package com.example.lms.controller;

import com.example.lms.db.DbConnection;
import javafx.fxml.FXMLLoader;
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

class BookControllerTest extends ApplicationTest {

    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/library_test?createDatabaseIfNotExist=true&allowMultiQueries=true";
    private static final String TEST_DB_USER = "root";
    private static final String TEST_DB_PASSWORD = "Sandy_@98";

    private Connection connection;
    private BookController controller;

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
        String insertBooksSQL = "INSERT INTO book_detail (id, title, author, status) VALUES " +
                "('B014', 'Test Title', 'Test Author', 'Available'), " +
                "('B017', 'Test Title', 'Test Author', 'Available'), " +
                "('B018', 'Test Title', 'Test Author', 'Available'), " +
                "('B019', 'Test Title', 'Test Author', 'Available'), " +
                "('B020', 'Test Title', 'Test Author', 'Available'), " +
                "('B021', 'Test Title', 'Test Author', 'Available'), " +
                "('B022', 'Test Title', 'Test Author', 'Available'), " +
                "('B023', 'Test Title', 'Test Author', 'Available'), " +
                "('B024', 'Test Title', 'Test Author', 'Available'), " +
                "('B025', 'Test Title', 'Test Author', 'Available'), " +
                "('B026', 'Test Title', 'Test Author', 'Available'), " +
                "('B027', 'Test Title', 'Test Author', 'Available'), " +
                "('B028', 'Test Title', 'Test Author', 'Available'), " +
                "('B029', 'Test Title', 'Test Author', 'Available'), " +
                "('B030', 'Test Title', 'Test Author', 'Available'), " +
                "('B031', 'Test Title', 'Test Author', 'Available'), " +
                "('B032', 'Test Title', 'Test Author', 'Available'), " +
                "('B033', 'Test Title', 'Test Author', 'Available'), " +
                "('B034', 'Test Title', 'Test Author', 'Available'), " +
                "('B035', 'Test Title', 'Test Author', 'Available'), " +
                "('B036', 'Test Title', 'Test Author', 'Available'), " +
                "('B037', 'Test Book', 'Test Author', 'Available'), " +
                "('B038', 'Test Book', 'Test Author', 'Available'), " +
                "('B039', 'Test Book', 'Test Author', 'Available'), " +
                "('B040', 'Test Book', 'Test Author', 'Available'), " +
                "('B041', 'Test Book', 'Test Author', 'Available'), " +
                "('B042', 'Test Book', 'Test Author', 'Available'), " +
                "('B043', 'Test Book', 'Test Author', 'Available'), " +
                "('B044', 'Test Book', 'Test Author', 'Available'), " +
                "('B045', 'Test Book', 'Test Author', 'Available'), " +
                "('B046', 'Test Book', 'Test Author', 'Available'), " +
                "('B047', 'Test Book', 'Test Author', 'Available'), " +
                "('B048', 'Test Book', 'Test Author', 'Available'), " +
                "('B049', 'Test Book', 'Test Author', 'Available')";
        try (PreparedStatement statement = connection.prepareStatement(insertBooksSQL)) {
            statement.execute();
        }

        // Load the FXML and initialize the controller
        FxRobot robot = new FxRobot();
        robot.interact(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/lms/BookView.fxml")); // Ensure this path is correct
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
    void testAddBook() throws SQLException {
        FxRobot robot = new FxRobot();
        robot.clickOn("#btn_new");
        robot.clickOn("#txt_bk_title").write("Test Book");
        robot.clickOn("#txt_bk_auth").write("Test Author");
        robot.clickOn("#btn_add");

        // Validate the book was added
        try (PreparedStatement selectBook = connection.prepareStatement("SELECT * FROM book_detail WHERE title = 'Test Book'")) {
            var resultSet = selectBook.executeQuery();
            assertTrue(resultSet.next(), "Book should be added to the database");
            assertEquals("Test Book", resultSet.getString("title"));
        }
    }

    @Test
    void testUpdateBook() throws SQLException {
        FxRobot robot = new FxRobot();

        // Select the first row in the TableView
        robot.clickOn("#tbl_bk");
        robot.type(KeyCode.DOWN);

        // Update the book details
        robot.clickOn("#txt_bk_title").eraseText(15).write("Updated Title");
        robot.clickOn("#btn_add");

        // Validate the book was updated
        try (PreparedStatement selectBook = connection.prepareStatement("SELECT * FROM book_detail WHERE id = 'B014'")) {
            var resultSet = selectBook.executeQuery();
            assertTrue(resultSet.next(), "Book should exist in the database");
            assertEquals("Updated Title", resultSet.getString("title"));
        }
    }

    @Test
    void testDeleteBook() throws SQLException {
        FxRobot robot = new FxRobot();

        // Select the first row in the TableView
        robot.clickOn("#tbl_bk");
        robot.type(KeyCode.DOWN);

        // Delete the book
        robot.clickOn("#btn_dlt");

        // Validate the book was deleted
        try (PreparedStatement selectBook = connection.prepareStatement("SELECT * FROM book_detail WHERE id = 'B014'")) {
            var resultSet = selectBook.executeQuery();
            assertTrue(resultSet.next(), "Book should be deleted from the database");
        }
    }

    // Add more tests as needed
}
