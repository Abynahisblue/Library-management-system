package com.example.lms.controller;

import com.example.lms.db.DbConnection;
import com.example.lms.model.Patron;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    // JDBC
    private Connection connection;
    private PreparedStatement newIdQuery;
    private PreparedStatement addToTable;
    private PreparedStatement updateQuery;
    private PreparedStatement select_patronID;

    public void initialize() throws ClassNotFoundException {
        // Disable id field
        mem_id.setDisable(true);
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Set table columns
        mem_tbl.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        mem_tbl.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        mem_tbl.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        mem_tbl.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("contact"));

        try {
            connection = DbConnection.getInstance().getConnection();
            ObservableList<Patron> members = FXCollections.observableArrayList();
            PreparedStatement select_all = connection.prepareStatement("SELECT * FROM member_detail");
            select_patronID = connection.prepareStatement("SELECT * FROM member_detail WHERE id=?");
            newIdQuery = connection.prepareStatement("SELECT id FROM member_detail");
            addToTable = connection.prepareStatement("INSERT INTO member_detail VALUES (?,?,?,?)");
            updateQuery = connection.prepareStatement("UPDATE member_detail SET name=?, address=?, contact=? WHERE id=?");
            ResultSet rst = select_all.executeQuery();
            while (rst.next()) {
                members.add(new Patron(
                        rst.getString(1),
                        rst.getString(2),
                        rst.getString(3),
                        rst.getString(4)
                ));
            }
            mem_tbl.setItems(members);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mem_tbl.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Patron>() {
            @Override
            public void changed(ObservableValue<? extends Patron> observable, Patron oldValue, Patron newValue) {
                if (newValue != null) {
                    Patron selectedItem = mem_tbl.getSelectionModel().getSelectedItem();
                    try {
                        select_patronID.setString(1, selectedItem.getId());
                        ResultSet rst = select_patronID.executeQuery();
                        if (rst.next()) {
                            mem_id.setText(rst.getString(1));
                            mem_name.setText(rst.getString(2));
                            mem_address.setText(rst.getString(3));
                            mem_num.setText(rst.getString(4));
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

    // Button new action
    public void btn_new(ActionEvent actionEvent) throws SQLException {
        mem_name.clear();
        mem_address.clear();
        mem_num.clear();
        btn_add.setText("Add");
        mem_id.setDisable(false);

        ResultSet rst = newIdQuery.executeQuery();

        String ids = null;
        int maxId = 0;

        while (rst.next()) {
            ids = rst.getString(1);
            int id = Integer.parseInt(ids.replace("M", ""));
            if (id > maxId) {
                maxId = id;
            }
        }
        maxId = maxId + 1;
        String id = "";
        if (maxId < 10) {
            id = "M00" + maxId;
        } else if (maxId < 100) {
            id = "M0" + maxId;
        } else {
            id = "M" + maxId;
        }
        mem_id.setText(id);
    }

    // Button add action
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

        // Save & update
        if (btn_add.getText().equals("Add")) {
            addToTable.setString(1, mem_id.getText());
            addToTable.setString(2, mem_name.getText());
            addToTable.setString(3, mem_address.getText());
            addToTable.setString(4, mem_num.getText());
            int affectedRows = addToTable.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Data load successful");
            } else {
                System.out.println("Something went wrong");
            }
        } else {
            if (btn_add.getText().equals("Update")) {
                updateQuery.setString(1, mem_name.getText());
                updateQuery.setString(2, mem_address.getText());
                updateQuery.setString(3, mem_num.getText());
                updateQuery.setString(4, mem_id.getText());
                int affected = updateQuery.executeUpdate();

                if (affected > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            "Record updated!!",
                            ButtonType.OK);
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Update error!",
                            ButtonType.OK);
                    alert.showAndWait();
                }
            }
        }
        refreshTable();
    }

    // Button delete action
    public void btn_dtl(ActionEvent actionEvent) throws SQLException {
        Patron selectedItem = mem_tbl.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Please select a member.",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        } else {
            PreparedStatement deleteQuery = connection.prepareStatement("DELETE FROM member_detail WHERE id=?");
            deleteQuery.setString(1, selectedItem.getId());

            int affected = deleteQuery.executeUpdate();
            if (affected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Record deleted!!",
                        ButtonType.OK);
                alert.showAndWait();
            }
        }
        refreshTable();
    }

    private void refreshTable() throws SQLException {
        mem_tbl.getItems().clear();
        try {
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
