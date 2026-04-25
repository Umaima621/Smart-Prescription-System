package model;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class MissedMedicinesUI {
    private static final String BG = "#E8EEF4";
    private static final String SURFACE = "#FFFFFF";
    private static final String BORDER = "#C8D4DC";

    public static VBox getView() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + BG + ";");

        Label title = new Label("Medicine Adherence Tracking");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        
        Label sub = new Label("The following doses were flagged as missed by patients.");
        sub.setStyle("-fx-text-fill: #7A8A96;");

        // --- TABLE AREA ---
        TableView<Object> table = new TableView<>(); // In real app, replace Object with a Model class
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 10; -fx-border-color: " + BORDER + ";");

        TableColumn<Object, String> timeCol = new TableColumn<>("Time Missed");
        TableColumn<Object, String> patientCol = new TableColumn<>("Patient Name");
        TableColumn<Object, String> medCol = new TableColumn<>("Medicine");
        TableColumn<Object, String> statusCol = new TableColumn<>("Patient Note");

        table.getColumns().addAll(timeCol, patientCol, medCol, statusCol);

        // Placeholder for when no data is found
        table.setPlaceholder(new Label("No missed doses reported for today."));

        root.getChildren().addAll(title, sub, table);
        return root;
    }
}