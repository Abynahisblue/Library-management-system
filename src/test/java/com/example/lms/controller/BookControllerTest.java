package com.example.lms.controller;

import com.example.lms.db.DbConnection;
import com.example.lms.model.Book;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerTest extends ApplicationTest {

    private BookController controller;
    private Connection connection;

    @Override
    public void start(Stage stage) throws Exception {
        controller = new BookController();
        controller.txt_bk_id = new TextField();
        controller.txt_bk_title = new TextField();
        controller.txt_bk_auth = new TextField();
        controller.txt_bk_st = new TextField();
        controller.tbl_bk = new TableView<>();
        controller.btn_add = new javafx.scene.control.Button();
        controller.btn_dlt = new javafx.scene.control.Button();
        controller.btn_new = new javafx.scene.control.Button();

        connection = DbConnection.getInstance().getConnection();
        controller.initialize();
    }

    @Test
    void testGenerateNewId() throws SQLException {
        String newId = controller.generateNewId();
        assertNotNull(newId);
        assertTrue(newId.startsWith("B"));
    }

    @Test
    void testLoadTableData() {
        controller.loadTableData();
        assertNotNull(controller.tbl_bk.getItems());
        assertFalse(controller.tbl_bk.getItems().isEmpty());
    }

    @Test
    void testTableColumns() {
        ObservableList<TableColumn<Book, ?>> columns = controller.tbl_bk.getColumns();
        assertEquals(4, columns.size());

        assertEquals("ID", columns.get(0).getText());
        assertEquals("Title", columns.get(1).getText());
        assertEquals("Author", columns.get(2).getText());
        assertEquals("Status", columns.get(3).getText());
    }

    @Test
    void testAddNewBook() throws SQLException {
        controller.btn_new(null);
        controller.txt_bk_title.setText("Test Title");
        controller.txt_bk_auth.setText("Test Author");
        controller.btn_Add(null);

        String sql = "SELECT * FROM book_detail WHERE id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, controller.txt_bk_id.getText());
            try (ResultSet rs = pstm.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("Test Title", rs.getString("title"));
                assertEquals("Test Author", rs.getString("author"));
                assertEquals("Available", rs.getString("status"));
            }
        }
    }

    @Test
    void testUpdateBook() throws SQLException {
        controller.btn_new(null);
        controller.txt_bk_title.setText("Test Title");
        controller.txt_bk_auth.setText("Test Author");
        controller.btn_Add(null);

        controller.txt_bk_title.setText("Updated Title");
        controller.btn_Add(null);

        String sql = "SELECT * FROM book_detail WHERE id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, controller.txt_bk_id.getText());
            try (ResultSet rs = pstm.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("Test Title", rs.getString("title"));
            }
        }
    }

    @Test
    void testDeleteBook() throws SQLException {
        controller.btn_new(null);
        controller.txt_bk_title.setText("Test Title");
        controller.txt_bk_auth.setText("Test Author");
        controller.btn_Add(null);

        controller.btn_dlt(null);

        String sql = "SELECT * FROM book_detail WHERE id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, controller.txt_bk_id.getText());
            try (ResultSet rs = pstm.executeQuery()) {
                assertTrue(rs.next());
            }
        }
    }
}
