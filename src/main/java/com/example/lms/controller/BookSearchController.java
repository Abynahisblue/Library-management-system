package com.example.lms.controller;

import com.example.lms.model.Book;
import com.example.lms.services.BookSearchService;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import java.sql.SQLException;

public class BookSearchController {
    public TextField bk_sch;
    public TableView<Book> tbl_bk;
    public AnchorPane sch_root;
    private BookSearchService bookSearchService;

    public void initialize() {
        // Initialize TableView columns with PropertyValueFactory
        tbl_bk.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tbl_bk.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("title"));
        tbl_bk.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("author"));
        tbl_bk.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("status"));

        try {
            bookSearchService = new BookSearchService();
            loadTableData();

            // Listener for text field to perform live search
            bk_sch.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    try {
                        // Clear TableView items
                        tbl_bk.getItems().clear();

                        // Fetch search results from database
                        ObservableList<Book> books = bookSearchService.searchBooks(newValue);
                        tbl_bk.setItems(books);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTableData() {
        try {
            ObservableList<Book> books = bookSearchService.getAllBooks();
            tbl_bk.setItems(books);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to navigate to another scene (HomeView.fxml)
    public void img_bk(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/com/example/lms/HomeView.fxml");
        assert resource != null;
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.sch_root.getScene().getWindow();
        primaryStage.setScene(scene);

        // Animation to transition to the new scene
        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }

    // Method to animate icon on mouse enter
    public void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            // Scale animation
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            // DropShadow effect
            DropShadow glow = new DropShadow();
            glow.setColor(Color.GREEN);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }
}
