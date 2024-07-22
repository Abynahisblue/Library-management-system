package com.example.lms.controller;

import com.example.lms.db.DbConnection;
import com.example.lms.model.Book;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookController {
    public TextField txt_bk_id;
    public TextField txt_bk_title;
    public TextField txt_bk_auth;
    public TextField txt_bk_st;
    public TableView<Book> tbl_bk;
    public AnchorPane bk_root;
    public Button btn_add;
    public Button btn_dlt;
    public Button btn_new;
    public ImageView img_back;

    // JDBC variables
    private Connection connection;
    private PreparedStatement selectAll;
    private PreparedStatement selectByID;
    private PreparedStatement newIdQuery;
    private PreparedStatement insertBook;
    private PreparedStatement updateBook;
    private PreparedStatement deleteBook;

    public void initialize() {
        txt_bk_id.setDisable(true);

        TableColumn<Book, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tbl_bk.getColumns().addAll(idColumn, titleColumn, authorColumn, statusColumn);

        try {
            connection = DbConnection.getInstance().getConnection();
            selectAll = connection.prepareStatement("SELECT * FROM book_detail");
            updateBook = connection.prepareStatement("UPDATE book_detail SET title = ?, author = ?, status = ? WHERE id = ?");
            selectByID = connection.prepareStatement("SELECT * FROM book_detail WHERE id = ?");
            insertBook = connection.prepareStatement("INSERT INTO book_detail VALUES (?, ?, ?, ?)");
            newIdQuery = connection.prepareStatement("SELECT id FROM book_detail");
            deleteBook = connection.prepareStatement("DELETE FROM book_detail WHERE id = ?");

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


    void loadTableData() {
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

    String generateNewId() throws SQLException {
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
        if (txt_bk_id.getText().isEmpty() || txt_bk_title.getText().isEmpty() || txt_bk_auth.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill in all details.");
            return;
        }

        if (!txt_bk_title.getText().matches("^\\b([A-Za-z.]+\\s?)+$") || !txt_bk_auth.getText().matches("^\\b([A-Za-z.]+\\s?)+$")) {
            showAlert(Alert.AlertType.ERROR, "Enter Valid Name.");
            return;
        }

        try {
            if (btn_add.getText().equals("Add")) {
                insertBook.setString(1, txt_bk_id.getText());
                insertBook.setString(2, txt_bk_title.getText());
                insertBook.setString(3, txt_bk_auth.getText());
                insertBook.setString(4, txt_bk_st.getText());
                int affectedRows = insertBook.executeUpdate();
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Book added successfully.");
                }
            } else {
                updateBook.setString(1, txt_bk_title.getText());
                updateBook.setString(2, txt_bk_auth.getText());
                updateBook.setString(3, txt_bk_st.getText());
                updateBook.setString(4, txt_bk_id.getText());
                int affectedRows = updateBook.executeUpdate();
                if (affectedRows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Book updated successfully.");
                }
            }
            refreshTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btn_dlt(ActionEvent actionEvent) {
        Book selectedBook = tbl_bk.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a book.");
            return;
        }

        try {
            // Check if the book is available
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
        String sql = "SELECT status FROM book_detail WHERE id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
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
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType, message, ButtonType.OK);
            alert.showAndWait();
        });
    }
}