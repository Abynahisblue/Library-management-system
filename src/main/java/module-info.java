module com.example.lms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.jfoenix;

    opens com.example.lms to javafx.fxml;
    opens com.example.lms.controller to javafx.fxml;
    opens com.example.lms.model to javafx.base; // This line is important

    exports com.example.lms;
    exports com.example.lms.controller;
    exports com.example.lms.model;
}
