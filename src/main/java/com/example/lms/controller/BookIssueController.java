package com.example.lms.controller;


import com.example.lms.db.DB;
import com.example.lms.model.Book;
import com.example.lms.model.BookIssued;
import com.example.lms.model.Patron;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;

public class BookIssueController {
    public TextField txt_issid;
    public DatePicker txt_isu_date;
    public TextField txt_name;
    public TextField txt_title;
    public ComboBox<String> mem_is_id;
    public ComboBox<String> book_id;
    public TableView<BookIssued> bk_issue_tbl;
    public AnchorPane bk_iss;

    public void initialize() {
        bk_issue_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("issueId"));
        bk_issue_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("date"));
        bk_issue_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("memberId"));
        bk_issue_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("bookId"));

        bk_issue_tbl.setItems(FXCollections.observableList(DB.bookIssued));

        mem_is_id.setItems(FXCollections.observableList(DB.patrons.stream().map(Patron::getId).toList()));
        book_id.setItems(FXCollections.observableList(DB.books.stream().map(Book::getId).toList()));

        mem_is_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    String selectedMemberId = mem_is_id.getSelectionModel().getSelectedItem();
                    txt_name.setText(DB.patrons.stream().filter(p -> p.getId().equals(selectedMemberId)).findFirst().orElseThrow().getName());
                }
            }
        });

        book_id.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    String selectedBookId = book_id.getSelectionModel().getSelectedItem();
                    txt_title.setText(DB.books.stream().filter(b -> b.getId().equals(selectedBookId)).findFirst().orElseThrow().getTitle());
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

            String memberId = mem_is_id.getSelectionModel().getSelectedItem();
            String bookId = book_id.getSelectionModel().getSelectedItem();

            BookIssued newIssuedBook = new BookIssued(txt_issid.getText(), txt_isu_date.getValue().toString(), memberId, bookId);
            DB.bookIssued.add(newIssuedBook);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Book issued successfully!", ButtonType.OK);
            successAlert.showAndWait();

            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public void back_click(MouseEvent event) {
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

    private void clearFields() {
        txt_issid.clear();
        txt_isu_date.setValue(null);
        txt_name.clear();
        txt_title.clear();
        mem_is_id.getSelectionModel().clearSelection();
        book_id.getSelectionModel().clearSelection();
    }
}
