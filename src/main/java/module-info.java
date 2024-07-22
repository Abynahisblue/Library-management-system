module com.example.lms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.junit.jupiter.api;
    //requires java.base;


    opens com.example.lms to javafx.fxml;
    opens com.example.lms.controller to javafx.fxml, javafx.base, junit;
    opens com.example.lms.db to junit;
    opens com.example.lms.model to junit;



    exports com.example.lms;
    exports com.example.lms.controller;
    exports com.example.lms.model;
}