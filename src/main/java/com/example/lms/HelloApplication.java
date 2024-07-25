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
        DbConnection.getInstance().close();

    }
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println(this.getClass().getResource("/com/example/lms/HomeView.fxml"));
            primaryStage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(this.getClass().getResource("/com/example/lms/HomeView.fxml")))));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error dialog)
        } catch (NullPointerException e) {
            e.printStackTrace();
            // Handle the null pointer exception (e.g., FXML file not found)
        }
    }

}