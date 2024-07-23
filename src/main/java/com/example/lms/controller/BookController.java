package com.example.lms.controller;

import com.example.lms.db.DbConnection;
import com.example.lms.model.Book;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;

public class BookController {
    public TextField txt_bk_id;
    public TextField txt_bk_title;
    public TextField txt_bk_auth;
    public TextField txt_bk_st;
    public TableView<Book> tbl_bk;
    public AnchorPane bk_root;
    public Button btn_add;

    private Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectByID;
    private PreparedStatement newIdQuery;
    private PreparedStatement insertBook;
    private PreparedStatement updateBook;
    private PreparedStatement deleteBook;

    private static final String SELECT_ALL_QUERY = "SELECT * FROM book_detail";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM book_detail WHERE id = ?";
    private static final String NEW_ID_QUERY = "SELECT id FROM book_detail";
    private static final String INSERT_BOOK_QUERY = "INSERT INTO book_detail VALUES (?, ?, ?, ?)";
    private static final String UPDATE_BOOK_QUERY = "UPDATE book_detail SET title = ?, author = ?, status = ? WHERE id = ?";
    private static final String DELETE_BOOK_QUERY = "DELETE FROM book_detail WHERE id = ?";
    private static final String CHECK_BOOK_STATUS_QUERY = "SELECT status FROM book_detail WHERE id = ?";

    public void initialize() {
        txt_bk_id.setDisable(true);

        tbl_bk.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl_bk.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tbl_bk.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tbl_bk.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));

        try {
            connection = DbConnection.getInstance().getConnection();
            selectAll = connection.prepareStatement(SELECT_ALL_QUERY);
            updateBook = connection.prepareStatement(UPDATE_BOOK_QUERY);
            selectByID = connection.prepareStatement(SELECT_BY_ID_QUERY);
            insertBook = connection.prepareStatement(INSERT_BOOK_QUERY);
            newIdQuery = connection.prepareStatement(NEW_ID_QUERY);
            deleteBook = connection.prepareStatement(DELETE_BOOK_QUERY);

            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tbl_bk.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadSelectedBookData(newValue);
            }
        });
    }

    private void loadTableData() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        try (ResultSet rst = selectAll.executeQuery()) {
            while (rst.next()) {
                books.add(new Book(
                        rst.getString("id"),
                        rst.getString("title"),
                        rst.getString("author"),
                        rst.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tbl_bk.setItems(books);
    }

    private void loadSelectedBookData(Book book) {
        try {
            selectByID.setString(1, book.getId());
            try (ResultSet rst = selectByID.executeQuery()) {
                if (rst.next()) {
                    txt_bk_id.setText(rst.getString("id"));
                    txt_bk_title.setText(rst.getString("title"));
                    txt_bk_auth.setText(rst.getString("author"));
                    txt_bk_st.setText(rst.getString("status"));
                    txt_bk_id.setDisable(true);
                    btn_add.setText("Update");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btn_new(ActionEvent actionEvent) {
        btn_add.setText("Add");
        txt_bk_st.setText("Available");
        txt_bk_st.setDisable(true);
        txt_bk_id.setDisable(false);
        txt_bk_auth.clear();
        txt_bk_title.clear();
        txt_bk_title.requestFocus();

        try {
            String newId = generateNewId();
            txt_bk_id.setText(newId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String generateNewId() throws SQLException {
        int maxId = 0;
        try (ResultSet rst = newIdQuery.executeQuery()) {
            while (rst.next()) {
                int id = Integer.parseInt(rst.getString("id").replace("B", ""));
                if (id > maxId) {
                    maxId = id;
                }
            }
        }
        maxId++;
        return String.format("B%03d", maxId);
    }

    public void btn_Add(ActionEvent actionEvent) {
        System.out.println("Button clicked");
        if (validateInputs()) {
            try {
                if (btn_add.getText().equals("Add")) {
                    insertBook.setString(1, txt_bk_id.getText());
                    insertBook.setString(2, txt_bk_title.getText());
                    insertBook.setString(3, txt_bk_auth.getText());
                    insertBook.setString(4, txt_bk_st.getText());
                    executeUpdate(insertBook, "Book added successfully.");
                } else {
                    updateBook.setString(1, txt_bk_title.getText());
                    updateBook.setString(2, txt_bk_auth.getText());
                    updateBook.setString(3, txt_bk_st.getText());
                    updateBook.setString(4, txt_bk_id.getText());
                    executeUpdate(updateBook, "Book updated successfully.");
                }
                refreshTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateInputs() {
        if (txt_bk_id.getText().isEmpty() || txt_bk_title.getText().isEmpty() || txt_bk_auth.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all details.");
            return false;
        }

        if (!txt_bk_title.getText().matches("^\\b([A-Za-z.]+\\s?)+$") || !txt_bk_auth.getText().matches("^\\b([A-Za-z.]+\\s?)+$")) {
            showAlert(Alert.AlertType.ERROR, "Enter Valid Name.");
            return false;
        }
        return true;
    }

    private void executeUpdate(PreparedStatement preparedStatement, String successMessage) throws SQLException {
        int affectedRows = preparedStatement.executeUpdate();
        System.out.println(affectedRows);
        if (affectedRows > 0) {
            System.out.println(successMessage);
            showAlert(Alert.AlertType.INFORMATION, successMessage);
        } else {
            System.out.println("No rows affected.");
        }
    }

    public void btn_dlt(ActionEvent actionEvent) {
        Book selectedBook = tbl_bk.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a book.");
            return;
        }

        try {
            if (!isBookAvailable(selectedBook.getId())) {
                showAlert(Alert.AlertType.ERROR, "The book cannot be deleted as it is currently unavailable.");
                return;
            }

            deleteBook.setString(1, selectedBook.getId());
            int affectedRows = deleteBook.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Book deleted successfully.");
                refreshTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isBookAvailable(String bookId) throws SQLException {
        try (PreparedStatement pstm = connection.prepareStatement(CHECK_BOOK_STATUS_QUERY)) {
            pstm.setString(1, bookId);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    return "Available".equalsIgnoreCase(status);
                } else {
                    throw new SQLException("Book ID not found: " + bookId);
                }
            }
        }
    }

    private void refreshTable() {
        tbl_bk.getItems().clear();
        loadTableData();
    }

    public void img_back(MouseEvent event) throws IOException {
        URL resource = getClass().getResource("/com/example/lms/HomeView.fxml");
        assert resource != null;
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) bk_root.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.YELLOW);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }
}
