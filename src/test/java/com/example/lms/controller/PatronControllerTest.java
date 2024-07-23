package com.example.lms.controller;

import com.example.lms.db.DbConnection;
import com.example.lms.model.Patron;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.service.query.PointQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatronControllerTest extends ApplicationTest {

    private PatronController controller;
    private Connection connection;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/lms/PatronView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() throws SQLException, ClassNotFoundException {
        connection = DbConnection.getInstance().getConnection();
        controller.initialize();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Clean up database after each test
        PreparedStatement deleteAll = connection.prepareStatement("DELETE FROM member_detail");
        deleteAll.executeUpdate();
    }

    @Test
    public void testAddNewPatron() {
        FxRobot robot = new FxRobot();
        robot.clickOn("#btn_new");

        robot.clickOn("#mem_name").write("John Doe");
        robot.clickOn("#mem_address").write("john.doe@example.com");
        robot.clickOn("#mem_num").write("1234567890");

        robot.clickOn("#btn_add");

        TableView<Patron> table = robot.lookup("#mem_tbl").query();
        assertEquals(1, table.getItems().size());

        Patron patron = table.getItems().getFirst();
        assertEquals("John Doe", patron.getName());
        assertEquals("john.doe@example.com", patron.getAddress());
        assertEquals("1234567890", patron.getContact());
    }

    @Test
    public void testUpdatePatron() throws SQLException {
        // Add a patron to update
        PreparedStatement insertPatron = connection.prepareStatement("INSERT INTO member_detail VALUES ('M001', 'Jane Doe', 'jane.doe@example.com', '0987654321')");
        insertPatron.executeUpdate();

        FxRobot robot = new FxRobot();
        robot.interact(() -> {
            try {
                controller.refreshTable();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        TableView<Patron> table = robot.lookup("#mem_tbl").query();
        robot.clickOn(table.getItems().getFirst().getName());

        robot.clickOn("#mem_name").eraseText(8).write("Jane Smith");
        robot.clickOn("#mem_address").eraseText(19).write("jane.smith@example.com");
        robot.clickOn("#mem_num").eraseText(10).write("1122334455");

        robot.clickOn("#btn_add");

        assertEquals(1, table.getItems().size());

        Patron patron = table.getItems().getFirst();
        assertEquals("Jane Smith", patron.getName());
        assertEquals("jane.smith@example.com", patron.getAddress());
        assertEquals("1122334455", patron.getContact());
    }

    @Test
    public void testDeletePatron() throws SQLException {
        // Add a patron to delete
        PreparedStatement insertPatron = connection.prepareStatement("INSERT INTO member_detail VALUES ('M001', 'Jane Doe', 'jane.doe@example.com', '0987654321')");
        insertPatron.executeUpdate();

        FxRobot robot = new FxRobot();
        robot.interact(() -> {
            try {
                controller.refreshTable();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        TableView<Patron> table = robot.lookup("#mem_tbl").query();
        robot.clickOn(table.getItems().getFirst().getName());

        robot.clickOn("#btn_dtl");

        assertEquals(1, table.getItems().size());
    }

    @Test
    public void testEmptyNameValidation() {
        FxRobot robot = new FxRobot();
        robot.clickOn("#btn_new");

        robot.clickOn("#mem_address").write("john.doe@example.com");
        robot.clickOn("#mem_num").write("1234567890");

        robot.clickOn("#btn_add");

        // Check for alert
        // Use CSS selector for Alert dialog
        Node alertDialog = robot.lookup(".alert").query();
        assertNotNull(alertDialog, "Alert dialog should be present");

        // Extract Alert dialog content
        DialogPane dialogPane = (DialogPane) alertDialog;
        assertEquals("Name cannot be empty", dialogPane.getContentText());

        // Close the alert dialog
        robot.clickOn(dialogPane.lookupButton(ButtonType.OK));
    }


    @Test
    public void testInvalidEmailValidation() {
        FxRobot robot = new FxRobot();
        robot.clickOn("#btn_new");

        robot.clickOn("#mem_name").write("John Doe");
        robot.clickOn("#mem_address").write("invalid-email");
        robot.clickOn("#mem_num").write("1234567890");

        robot.clickOn("#btn_add");

        // Check for alert
        // Use CSS selector for Alert dialog
        Node alertDialog = robot.lookup(".alert").query();
        assertNotNull(alertDialog, "Alert dialog should be present");

        // Extract Alert dialog content
        DialogPane dialogPane = (DialogPane) alertDialog;
        assertEquals("Address should be a valid email", dialogPane.getContentText());

        // Close the alert dialog
        robot.clickOn(dialogPane.lookupButton(ButtonType.OK));
    }

    @Test
    public void testInvalidPhoneNumberValidation() {
        FxRobot robot = new FxRobot();
        robot.clickOn("#btn_new");

        robot.clickOn("#mem_name").write("John Doe");
        robot.clickOn("#mem_address").write("john.doe@example.com");
        robot.clickOn("#mem_num").write("12345");

        robot.clickOn("#btn_add");

        // Check for alert
        // Use CSS selector for Alert dialog
        Node alertDialog = robot.lookup(".alert").query();
        assertNotNull(alertDialog, "Alert dialog should be present");

        // Extract Alert dialog content
        DialogPane dialogPane = (DialogPane) alertDialog;
        assertEquals("Number should be 10 digits", dialogPane.getContentText());

        // Close the alert dialog
        robot.clickOn(dialogPane.lookupButton(ButtonType.OK));
    }
}
