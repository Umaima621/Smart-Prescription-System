package model;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class PrescribeMedicineUI {
    private static final String BG = "#E8EEF4";
    private static final String SURFACE = "#FFFFFF";
    private static final String BORDER = "#C8D4DC";
    private static final String TEAL = "#2D7A6B";

    public static VBox getView() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + BG + ";");

        Label title = new Label("Create New Prescription");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        // --- FORM CARD ---
        VBox card = new VBox(20);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: " + SURFACE + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + ";");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        // Fields
        TextField patientId = buildStyledField("e.g. PT-9920");
        TextField medName = buildStyledField("Enter medicine name...");
        ComboBox<String> dosage = new ComboBox<>();
        dosage.getItems().addAll("Once Daily", "Twice Daily", "Every 8 Hours", "As Needed");
        dosage.setPrefWidth(300);
        
        TextArea instructions = new TextArea();
        instructions.setPromptText("Special instructions for the patient...");
        instructions.setPrefHeight(100);

        // Adding to grid
        addFormRow(grid, "Patient ID:", patientId, 0);
        addFormRow(grid, "Medication:", medName, 1);
        addFormRow(grid, "Dosage Frequency:", dosage, 2);
        addFormRow(grid, "Instructions:", instructions, 3);

        Button submitBtn = new Button("Issue Prescription");
        submitBtn.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 30; -fx-cursor: hand;");

        card.getChildren().addAll(grid, new Separator(), submitBtn);
        root.getChildren().addAll(title, card);

        return root;
    }

    private static void addFormRow(GridPane gp, String label, javafx.scene.Node field, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #7A8A96;");
        gp.add(lbl, 0, row);
        gp.add(field, 1, row);
    }

    private static TextField buildStyledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMinHeight(35);
        tf.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 5;");
        return tf;
    }
}