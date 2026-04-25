package model;


import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class PatientSearchUI {

    // Using the same design system constants
    private static final String BG = "#E8EEF4";
    private static final String SURFACE = "#FFFFFF";
    private static final String BORDER = "#C8D4DC";
    private static final String TEAL = "#2D7A6B";

    public static VBox getView() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + BG + ";");

        // --- SEARCH SECTION ---
        VBox searchSection = new VBox(10);
        Label searchLabel = new Label("Find Patient Record");
        searchLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        
        HBox searchBar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Enter Patient ID or Name...");
        searchField.setPrefWidth(400);
        searchField.setMinHeight(40);
        searchField.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER + "; -fx-border-radius: 8;");

        Button searchBtn = new Button("Search");
        searchBtn.setMinHeight(40);
        searchBtn.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 0 20; -fx-cursor: hand;");
        
        searchBar.getChildren().addAll(searchField, searchBtn);
        searchSection.getChildren().addAll(searchLabel, searchBar);

        // --- PROFILE DISPLAY AREA (For CRUD Integration) ---
        VBox profileCard = new VBox(20);
        profileCard.setPadding(new Insets(25));
        profileCard.setStyle("-fx-background-color: " + SURFACE + "; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-width: 1;");
        
        Label profileTitle = new Label("Patient Information");
        profileTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        GridPane details = new GridPane();
        details.setHgap(40);
        details.setVgap(15);
        
        // Placeholders for CRUD data
        addDetailRow(details, "Full Name:", "John Doe", 0);
        addDetailRow(details, "Patient ID:", "PT-9920", 1);
        addDetailRow(details, "Blood Type:", "O+", 2);
        addDetailRow(details, "Last Appointment:", "2024-03-15", 3);

        VBox historyArea = new VBox(10);
        Label historyLabel = new Label("Medical History Summary:");
        historyLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        TextArea historyText = new TextArea("No recent major surgeries. Chronic asthma noted.");
        historyText.setEditable(false);
        historyText.setPrefHeight(100);
        historyText.setWrapText(true);
        historyArea.getChildren().addAll(historyLabel, historyText);

        profileCard.getChildren().addAll(profileTitle, new Separator(), details, historyArea);

        root.getChildren().addAll(searchSection, profileCard);
        return root;
    }

    private static void addDetailRow(GridPane gp, String label, String value, int row) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #7A8A96; -fx-font-weight: bold;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #2C3035;");
        gp.add(lbl, 0, row);
        gp.add(val, 1, row);
    }
}