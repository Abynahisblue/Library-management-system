module com.example.lms {
    exports com.example.lms.controller;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.jfoenix;


    opens com.example.lms to javafx.fxml;
    exports com.example.lms;
}