package com.example.lms.controller;

import com.example.lms.model.Patron;
import com.example.lms.services.PatronService;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.sql.SQLException;
import java.util.List;


public class PatronController {

    public TextField mem_id;
    public TextField mem_name;
    public TextField mem_address;
    public TextField mem_num;
    public TableView<Patron> mem_tbl;
    public ImageView img_bk;
    public AnchorPane root;
    public Button btn_new;
    public Button btn_add;

    private PatronService patronService;

    public void initialize() {
        // Disable id field
        mem_id.setDisable(true);

        // Set table columns
        mem_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        mem_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        mem_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        mem_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("contact"));

        try {
            patronService = new PatronService();
            loadTableData();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        mem_tbl.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Patron>() {
            @Override
            public void changed(ObservableValue<? extends Patron> observable, Patron oldValue, Patron newValue) {
                if (newValue != null) {
                    Patron selectedItem = mem_tbl.getSelectionModel().getSelectedItem();
                    try {
                        Patron patron = patronService.getPatronById(selectedItem.getId());
                        if (patron != null) {
                            mem_id.setText(patron.getId());
                            mem_name.setText(patron.getName());
                            mem_address.setText(patron.getAddress());
                            mem_num.setText(patron.getContact());
                            mem_id.setDisable(true);
                            btn_add.setText("Update");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadTableData() {
        ObservableList<Patron> members = FXCollections.observableArrayList();
        try {
            List<Patron> patrons = patronService.getAllPatrons();
            members.addAll(patrons);
            mem_tbl.setItems(members);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btn_new(ActionEvent actionEvent) throws SQLException {
        mem_name.clear();
        mem_address.clear();
        mem_num.clear();
        btn_add.setText("Add");
        mem_id.setDisable(false);

        String id = patronService.generateNewId();
        mem_id.setText(id);
    }

    public void btn_add(ActionEvent actionEvent) throws SQLException {
        ObservableList<Patron> members = mem_tbl.getItems();
        // Is empty

        if(mem_name.getText().isEmpty()){
            new Alert(Alert.AlertType.ERROR, "Name cannot be empty").show();
            return;
        }

        if(mem_address.getText().isEmpty() || !mem_address.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            new Alert(Alert.AlertType.ERROR, "Address should be a valid email").show();
            return;
        }
        // Reg-ex
        if (mem_num.getText().isEmpty() || !mem_num.getText().matches("\\d{10}")) {
            new Alert(Alert.AlertType.ERROR, "Number should be 10 digits").show();
            return;
        }

        Patron patron = new Patron(mem_id.getText(), mem_name.getText(), mem_address.getText(), mem_num.getText());

        if (btn_add.getText().equals("Add")) {
            patronService.addPatron(patron);
        } else {
            patronService.updatePatron(patron);
        }
        refreshTable();
    }

    public void btn_dtl(ActionEvent actionEvent) throws SQLException {
        Patron selectedItem = mem_tbl.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please select a member.",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        } else {
            patronService.deletePatron(selectedItem.getId());
        }
        refreshTable();
    }

    void refreshTable() {
        mem_tbl.getItems().clear();
        loadTableData();
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

    public void img_back(MouseEvent event) throws IOException {
        URL resource = this.getClass().getResource("/com/example/lms/HomeView.fxml");
        assert resource != null;
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) this.root.getScene().getWindow();
        primaryStage.setScene(scene);

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), scene.getRoot());
        tt.setFromX(-scene.getWidth());
        tt.setToX(0);
        tt.play();
    }
}
