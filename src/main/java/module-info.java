module com.example.lms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.jfoenix;
    //requires java.base;

    opens com.example.lms to javafx.fxml;
    opens com.example.lms.controller to javafx.fxml;
    opens com.example.lms.model to javafx.base;
    //opens java.lang.reflect to com.jfoenix;

    exports com.example.lms;
    exports com.example.lms.controller;
    exports com.example.lms.model;
}
