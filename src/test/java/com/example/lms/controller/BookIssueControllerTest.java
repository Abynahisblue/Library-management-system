package com.example.lms.controller;

import com.example.lms.db.DB;
import com.example.lms.db.DbConnection;
import com.example.lms.model.Book;
import com.example.lms.model.BookIssued;
import com.example.lms.model.Patron;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookIssueControllerTest extends ApplicationTest {

    private BookIssueController controller;
    private Connection connection;

    @Override
    public void start(Stage stage) throws Exception {
        controller = new BookIssueController();
        controller.txt_issid = new TextField();
        controller.txt_isu_date = new DatePicker();
        controller.txt_name = new TextField();
        controller.txt_title = new TextField();
        controller.mem_is_id = new ComboBox<>();
        controller.book_id = new ComboBox<>();
        controller.bk_issue_tbl = new TableView<>();
        controller.bk_iss = new javafx.scene.layout.AnchorPane();

        connection = DbConnection.getInstance().getConnection();
        controller.initialize();
    }

    @Test
    void testInitialize() {
        Platform.runLater(() -> {
            assertNotNull(controller.bk_issue_tbl.getItems());
            assertNotNull(controller.mem_is_id.getItems());
            assertNotNull(controller.book_id.getItems());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @Disabled
    void testAddNewBookIssue() throws SQLException {
        Platform.runLater(() -> {
            controller.new_action(null);
            controller.txt_isu_date.setValue(LocalDate.now());
            controller.mem_is_id.setValue("M001");
            controller.book_id.setValue("B002");
            controller.add_Action(null);
        });
        WaitForAsyncUtils.waitForFxEvents();

        String issueId = controller.txt_issid.getText();
        String sql = "SELECT * FROM issue_table WHERE issueId = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, issueId);
            try (ResultSet rs = pstm.executeQuery()) {
                assertTrue(rs.next());
                assertEquals("M001", rs.getString("patronId"));
                assertEquals("B002", rs.getString("bookId"));
            }
        }
    }

    @Test
    //@Disabled
    void testDeleteBookIssue() throws SQLException {
        Platform.runLater(() -> {
            controller.new_action(null);
            controller.txt_isu_date.setValue(LocalDate.now());
            controller.mem_is_id.setValue("M001");
            controller.book_id.setValue("B002");
            controller.add_Action(null);
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            controller.bk_issue_tbl.getSelectionModel().select(0);
            controller.delete_Action(null);
        });
        WaitForAsyncUtils.waitForFxEvents();

        String issueId = controller.txt_issid.getText();
        String sql = "SELECT * FROM issue_table WHERE issueId = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, issueId);
            try (ResultSet rs = pstm.executeQuery()) {
                assertFalse(rs.next());
            }
        }
    }

    @Test
    void testLoadTableData() {
        Platform.runLater(() -> {
            controller.loadTableData();
            assertNotNull(controller.bk_issue_tbl.getItems());
            assertFalse(controller.bk_issue_tbl.getItems().isEmpty());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
