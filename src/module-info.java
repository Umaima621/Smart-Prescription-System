module Smart_Prescription {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.sql;

    exports ui;
    exports controller;
    exports controller.strategy;
    exports dao;
    exports db;
    exports model;
}