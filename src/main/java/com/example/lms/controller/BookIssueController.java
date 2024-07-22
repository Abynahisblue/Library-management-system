package com.example.lms.controller;


import com.example.lms.db.DB;
import com.example.lms.db.DbConnection;
import com.example.lms.model.Book;
import com.example.lms.model.BookIssued;
import com.example.lms.model.Patron;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class BookIssueController {
    @FXML
    public TextField txt_issid;
    @FXML
    public DatePicker txt_isu_date;
    @FXML
    public TextField txt_name;
    @FXML
    public TextField txt_title;
    @FXML
    public ComboBox<String> mem_is_id;
    @FXML
    public ComboBox<String> book_id;
    @FXML
    public TableView<BookIssued> bk_issue_tbl;
    @FXML
    public AnchorPane bk_iss;

    private Connection connection;
    private PreparedStatement selectAll;


    @FXML
    public void initialize() {
        // Load data from the database
        DB.loadBooks();
        DB.loadPatrons();
        DB.loadBooksIssued();

        // Initialize table columns
        bk_issue_tbl.setDisable(true); // This disables the TableView initially

        TableColumn<BookIssued, String> issueIdColumn = new TableColumn<>("Issue ID");
        issueIdColumn.setCellValueFactory(new PropertyValueFactory<>("issueId"));

        TableColumn<BookIssued, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<BookIssued, String> patronIdColumn = new TableColumn<>("Patron ID");
        patronIdColumn.setCellValueFactory(new PropertyValueFactory<>("patronId"));

        TableColumn<BookIssued, String> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));

        bk_issue_tbl.getColumns().addAll(issueIdColumn, dateColumn, patronIdColumn, bookIdColumn);

        // Establish a connection and load table data
        try {
            connection = DbConnection.getInstance().getConnection();
            selectAll = connection.prepareStatement("SELECT * FROM issue_table");
            loadTableData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set items for the table if bookIssued list is not empty
        if (!DB.bookIssued.isEmpty()) {
            bk_issue_tbl.setItems(FXCollections.observableList(DB.bookIssued));
        }

        // Clear ComboBox selections
        mem_is_id.getSelectionModel().clearSelection();
        book_id.getSelectionModel().clearSelection();

        // Populate ComboBoxes if lists are not empty
        if (!DB.patrons.isEmpty()) {
            mem_is_id.setItems(FXCollections.observableList(DB.patrons.stream().map(Patron::getId).distinct().toList()));
        }
        if (!DB.books.isEmpty()) {
            book_id.setItems(FXCollections.observableList(DB.books.stream().map(Book::getId).distinct().toList()));
        }

        // Add listener to member ComboBox
        mem_is_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    String selectedMemberId = mem_is_id.getSelectionModel().getSelectedItem();
                    DB.patrons.stream()
                            .filter(p -> p.getId().equals(selectedMemberId))
                            .findFirst()
                            .ifPresentOrElse(
                                    patron -> txt_name.setText(patron.getName()),
                                    () -> txt_name.setText("Member not found")
                            );
                }
            }
        });

        // Add listener to book ComboBox
        book_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    String selectedBookId = book_id.getSelectionModel().getSelectedItem();
                    DB.books.stream()
                            .filter(b -> b.getId().equals(selectedBookId))
                            .findFirst()
                            .ifPresentOrElse(
                                    book -> txt_title.setText(book.getTitle()),
                                    () -> txt_title.setText("Book not found")
                            );
                }
            }
        });
    }

    public void new_action(ActionEvent actionEvent) {
        txt_title.clear();
        txt_name.clear();
        mem_is_id.getSelectionModel().clearSelection();
        book_id.getSelectionModel().clearSelection();
        txt_isu_date.setPromptText("Issue Date");

        int maxId = DB.bookIssued.stream().mapToInt(bi -> Integer.parseInt(bi.getIssueId().replace("I", ""))).max().orElse(0);
        maxId++;
        String id = (maxId < 10) ? "I00" + maxId : (maxId < 100) ? "I0" + maxId : "I" + maxId;
        txt_issid.setText(id);
    }

    public void add_Action(ActionEvent actionEvent) {
        try {
            if (txt_issid.getText().isEmpty() ||
                    book_id.getSelectionModel().getSelectedItem() == null ||
                    mem_is_id.getSelectionModel().getSelectedItem() == null ||
                    txt_isu_date.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all details.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            String patronId = mem_is_id.getSelectionModel().getSelectedItem();
            String bookId = book_id.getSelectionModel().getSelectedItem();
            String issueId = txt_issid.getText();
            String issueDate = txt_isu_date.getValue().toString();

            // Check if the book is available
            if (!isBookAvailable(bookId)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The book is currently unavailable.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            BookIssued newIssuedBook = new BookIssued(issueId, issueDate, patronId, bookId);
            DB.bookIssued.add(newIssuedBook);

            String insertSQL = "INSERT INTO issue_table (issueId, date, patronId, bookId) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertSQL)) {
                insertStatement.setString(1, issueId);
                insertStatement.setString(2, issueDate);
                insertStatement.setString(3, patronId);
                insertStatement.setString(4, bookId);

                int affectedRows = insertStatement.executeUpdate();
                if (affectedRows > 0) {
                    updateBookStatusToUnavailable(issueId);

                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Book issued successfully!", ButtonType.OK);
                    successAlert.showAndWait();
                    refreshTable();
                } else {
                    Alert failureAlert = new Alert(Alert.AlertType.ERROR, "Failed to issue book. Please try again.", ButtonType.OK);
                    failureAlert.showAndWait();
                }
            }

            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateBookStatusToUnavailable(String issueID) throws SQLException {
        String bookId = getBookIdFromIssue(issueID);
        String sql = "UPDATE book_detail SET status = 'Unavailable' WHERE id = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, bookId);
            pstm.executeUpdate();
        }
    }
    private String getBookIdFromIssue(String issueID) throws SQLException {
        String sql = "SELECT bookId FROM issue_table WHERE issueId = ?";
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, issueID);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("bookId");
                } else {
                    throw new SQLException("Issue ID not found: " + issueID);
                }
            }
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

    public void delete_Action(ActionEvent actionEvent) {
        BookIssued selectedBook = bk_issue_tbl.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a book to delete.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            DB.bookIssued.remove(selectedBook);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Book issue record deleted successfully!", ButtonType.OK);
            successAlert.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void loadTableData() {
        DB.bookIssued.clear();
        ObservableList<BookIssued> booksIssued = FXCollections.observableList(DB.bookIssued);
        try (ResultSet rst = selectAll.executeQuery()) {
            while (rst.next()) {
                booksIssued.add(new BookIssued(
                        rst.getString("issueId"),
                        rst.getString("date"),
                        rst.getString("patronId"),
                        rst.getString("bookId")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bk_issue_tbl.setItems(booksIssued);
    }

    public void back_click(MouseEvent event){
        try {
            URL resource = getClass().getResource("/com/example/lms/HomeView.fxml");
            assert resource != null;
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            Stage primaryStage = (Stage) bk_iss.getScene().getWindow();
            primaryStage.setScene(scene);

            TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
            tt.setFromX(-scene.getWidth());
            tt.setToX(0);
            tt.play();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

    private void refreshTable() throws SQLException {
        bk_issue_tbl.getItems().clear();
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txt_issid.clear();
        txt_isu_date.setValue(null);
        txt_name.clear();
        txt_title.clear();
        mem_is_id.getSelectionModel().clearSelection();
        book_id.getSelectionModel().clearSelection();
    }
}
