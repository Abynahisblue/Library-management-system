package com.example.lms.controller;

import com.example.lms.model.Book;
import com.example.lms.services.BookService;
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
import java.util.List;

public class BookController {
    public TextField txt_bk_id;
    public TextField txt_bk_title;
    public TextField txt_bk_auth;
    public TextField txt_bk_st;
    public TableView<Book> tbl_bk;
    public AnchorPane bk_root;
    public Button btn_add;

    private final BookService bookService = new BookService(DriverManager.getConnection("jdbc:mysql://localhost:3306/library","root","Sandy_@98"));

    public BookController() throws SQLException {
    }

    public void initialize() throws SQLException {
        txt_bk_id.setDisable(true);

        tbl_bk.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl_bk.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tbl_bk.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tbl_bk.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));

        loadTableData();

        tbl_bk.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadSelectedBookData(newValue);
            }
        });
    }

    private void loadTableData() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        try {
            List<Book> bookList = BookService.getAllBooks();
            books.addAll(bookList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tbl_bk.setItems(books);
    }


    private void loadSelectedBookData(Book book) {
                    txt_bk_id.setText(book.getId());
                    txt_bk_title.setText(book.getTitle());
                    txt_bk_auth.setText(book.getAuthor());
                    txt_bk_st.setText(book.getStatus());
                    txt_bk_id.setDisable(true);
                    btn_add.setText("Update");

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
            String newId = BookService.generateNewId();
            txt_bk_id.setText(newId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void btn_Add(ActionEvent actionEvent) {
        System.out.println("Button clicked");
        if (validateInputs()) {
            try {
                if (btn_add.getText().equals("Add")) {
                   showAlert(Alert.AlertType.INFORMATION,bookService.addBook(new Book(txt_bk_title.getText(),txt_bk_auth.getText(),txt_bk_st.getText(),txt_bk_id.getText())));
                } else {
                    showAlert(Alert.AlertType.INFORMATION,bookService.updateBook(new Book(txt_bk_title.getText(),txt_bk_auth.getText(),txt_bk_st.getText(),txt_bk_id.getText())));
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



    public void btn_dlt(ActionEvent actionEvent) {
        Book selectedBook = tbl_bk.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a book.");
            return;
        }

        try {
            if (!bookService.isBookAvailable(selectedBook.getId())) {
                showAlert(Alert.AlertType.ERROR, "The book cannot be deleted as it is currently unavailable.");
                return;
            }

            if (bookService.deleteBook(selectedBook.getId()) > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Book deleted successfully.");
                refreshTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void refreshTable() throws SQLException {
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
