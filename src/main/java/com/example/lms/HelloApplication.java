package com.example.lms;

import com.example.lms.db.DbConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class HelloApplication extends Application {
    public static void main(String[] args) throws SQLException {
        launch();
        DbConnection.getInstance().getConnection().close();

    }
    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(new Scene((FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("/View/HomeFormView.fxml"))))));
        stage.show();
    }


}