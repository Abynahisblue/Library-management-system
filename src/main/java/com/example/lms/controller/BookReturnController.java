package com.example.lms.controller;

import com.example.lms.model.BookReturn;
import com.example.lms.services.BookReturnService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import java.time.LocalDate;

public class BookReturnController {
    public AnchorPane Return_root;
    @FXML
    public TextField txt_issue_date;
    public TextField txt_fine;
    public DatePicker txt_rt_date;
    public TableView<BookReturn> rt_tbl;
    public ComboBox<String> cmb_issue_id;
    private BookReturnService bookReturnService;

    public void initialize() {
        try {
            bookReturnService = new BookReturnService();
            initializeTableColumns();
            loadReturnDetails();
            loadIssueIds();

            cmb_issue_id.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateIssueDate(newValue);
                }
            });

            txt_rt_date.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    calculateFine();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeTableColumns() {
        rt_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        rt_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("issuedDate"));
        rt_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("returnedDate"));
        rt_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("fine"));
    }

    private void loadReturnDetails() {
        try {
            ObservableList<BookReturn> returns = bookReturnService.getAllReturns();
            rt_tbl.setItems(returns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadIssueIds() {
        try {
            ObservableList<String> issueIds = bookReturnService.getIssueIds();
            cmb_issue_id.setItems(issueIds);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateIssueDate(String issueId) {
        try {
            String issueDate = bookReturnService.getIssueDate(issueId);
            if (issueDate != null) {
                txt_issue_date.setText(issueDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calculateFine() {
        LocalDate returnedDate = txt_rt_date.getValue();
        LocalDate issuedDate = LocalDate.parse(txt_issue_date.getText());

        float fine = bookReturnService.calculateFine(issuedDate, returnedDate);
        txt_fine.setText(Float.toString(fine));
    }

    public void btn_new(ActionEvent actionEvent) {
        txt_fine.clear();
        txt_issue_date.clear();
        cmb_issue_id.getSelectionModel().clearSelection();
        txt_rt_date.setValue(null);
    }

    public void btn_add(ActionEvent actionEvent) {
        if (cmb_issue_id.getSelectionModel().isEmpty() || txt_issue_date.getText().isEmpty() || txt_rt_date.getValue() == null || txt_fine.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please fill all details.");
            return;
        }

        try {
            String issueID = cmb_issue_id.getValue();
            String issuedDate = txt_issue_date.getText();
            String returnedDate = txt_rt_date.getValue().toString();
            String fine = txt_fine.getText();

            bookReturnService.addReturn(issueID, issuedDate, returnedDate, fine);

            showAlert(Alert.AlertType.INFORMATION, "Return successfully recorded and status updated.");
            refreshTable();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        rt_tbl.getItems().clear();
        loadReturnDetails();
    }

    private void clearFields() {
        cmb_issue_id.getSelectionModel().clearSelection();
        txt_issue_date.clear();
        txt_fine.clear();
        txt_rt_date.setValue(null);
    }

    public void img_back(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/com/example/lms/HomeView.fxml");
        assert resource != null;
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.Return_root.getScene().getWindow();
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

    public void closeServiceConnection() {
        try {
            if (bookReturnService != null) {
                bookReturnService.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
