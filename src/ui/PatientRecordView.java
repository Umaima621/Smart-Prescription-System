package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PatientRecordView extends VBox {

    private TextField searchField;
    private Label patientInfoLabel;
    private TextArea medicalHistoryArea;
    private TableView<PrescriptionRecord> prescriptionTable;

    public PatientRecordView() {
        setSpacing(0);
        setPadding(new Insets(0));
        buildUI();
    }

    private void buildUI() {
        VBox header = new VBox(12);
        header.setPadding(new Insets(UITheme.PADDING_LARGE));
        header.setStyle(UITheme.getMainContainerStyle());

        Label title = new Label("View Patient Record");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());

        Label subtitle = new Label("Access full patient history and medical records");
        subtitle.setFont(UITheme.fontSmall());
        subtitle.setTextFill(UITheme.colorTextMuted());

        header.getChildren().addAll(title, subtitle);

        VBox searchCard = new VBox(UITheme.GAP_MEDIUM);
        searchCard.setPadding(new Insets(UITheme.PADDING_LARGE));
        searchCard.setStyle(UITheme.getCardStyle());
        searchCard.setMaxWidth(600);

        HBox searchBox = new HBox(UITheme.GAP_MEDIUM);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        Label searchLbl = new Label("Patient ID or Name:");
        searchLbl.setFont(UITheme.fontLabel());
        searchLbl.setStyle(UITheme.getLabelStyle());

        searchField = new TextField();
        searchField.setPromptText("e.g., PT-9920 or John Smith");
        searchField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        searchField.setStyle(UITheme.getTextFieldStyle());
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle(UITheme.getPrimaryButtonStyle());
        searchBtn.setMinWidth(100);
        searchBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        searchBtn.setOnAction(e -> handleSearch());

        searchBox.getChildren().addAll(searchLbl, searchField, searchBtn);
        searchCard.getChildren().add(searchBox);

        VBox infoCard = new VBox(UITheme.GAP_MEDIUM);
        infoCard.setPadding(new Insets(UITheme.PADDING_LARGE));
        infoCard.setStyle(UITheme.getCardStyle());

        patientInfoLabel = new Label("No patient selected");
        patientInfoLabel.setFont(UITheme.fontHeading4());
        patientInfoLabel.setTextFill(UITheme.colorTextPrimary());

        Label histLbl = new Label("Medical History:");
        histLbl.setFont(UITheme.fontLabel());
        histLbl.setStyle(UITheme.getLabelStyle());

        medicalHistoryArea = new TextArea();
        medicalHistoryArea.setPromptText("Medical history will appear here...");
        medicalHistoryArea.setPrefHeight(100);
        medicalHistoryArea.setWrapText(true);
        medicalHistoryArea.setStyle(UITheme.getTextFieldStyle());

        infoCard.getChildren().addAll(patientInfoLabel, new Separator(), histLbl, medicalHistoryArea);

        VBox prescCard = new VBox(UITheme.GAP_MEDIUM);
        prescCard.setPadding(new Insets(UITheme.PADDING_LARGE));
        prescCard.setStyle(UITheme.getCardStyle());

        Label prescLbl = new Label("Active Prescriptions:");
        prescLbl.setFont(UITheme.fontLabel());
        prescLbl.setStyle(UITheme.getLabelStyle());

        prescriptionTable = new TableView<>();
        prescriptionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        prescriptionTable.setStyle("-fx-background-radius: 10;");

        TableColumn<PrescriptionRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().date));

        TableColumn<PrescriptionRecord, String> medCol = new TableColumn<>("Medicine");
        medCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().medicine));

        TableColumn<PrescriptionRecord, String> dosageCol = new TableColumn<>("Dosage");
        dosageCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().dosage));

        prescriptionTable.getColumns().add(dateCol);
        prescriptionTable.getColumns().add(medCol);
        prescriptionTable.getColumns().add(dosageCol);
        prescriptionTable.setPlaceholder(new Label("No prescriptions found."));

        prescCard.getChildren().addAll(prescLbl, prescriptionTable);

        VBox content = new VBox(UITheme.GAP_LARGE);
        content.setPadding(new Insets(UITheme.PADDING_MEDIUM));
        content.setStyle(UITheme.getMainContainerStyle());
        content.getChildren().addAll(searchCard, infoCard, prescCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(UITheme.getMainContainerStyle());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane);
    }

    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            patientInfoLabel.setText("Please enter a patient ID or name");
            return;
        }

        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();

            String sql = "SELECT * FROM Users WHERE (name LIKE ? OR email LIKE ?) AND role = 'Patient'";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String patientName = rs.getString("name");
                String email = rs.getString("email");
                String bloodType = rs.getString("bloodType");
                String medHistory = rs.getString("medicalHistory");
                String userID = rs.getString("userID");

                patientInfoLabel.setText("Patient: " + patientName + " | Email: " + email
                        + " | Blood Type: " + (bloodType != null ? bloodType : "N/A"));

                medicalHistoryArea.setText(medHistory != null ? medHistory : "No medical history recorded.");

                prescriptionTable.getItems().clear();
                String sql2 = "SELECT * FROM Prescriptions WHERE patientID = ? ORDER BY prescribedDate DESC";
                java.sql.PreparedStatement stmt2 = conn.prepareStatement(sql2);
                stmt2.setString(1, userID);
                java.sql.ResultSet rs2 = stmt2.executeQuery();

                while (rs2.next()) {
                    prescriptionTable.getItems().add(new PrescriptionRecord(
                            rs2.getString("prescribedDate"),
                            rs2.getString("medicineName"),
                            rs2.getString("dosageFrequency")
                    ));
                }

                if (prescriptionTable.getItems().isEmpty()) {
                    prescriptionTable.setPlaceholder(new Label("No prescriptions found for this patient."));
                }

            } else {
                patientInfoLabel.setText("No patient found for: " + query);
                medicalHistoryArea.clear();
                prescriptionTable.getItems().clear();
            }

        } catch (java.sql.SQLException e) {
            patientInfoLabel.setText("Database error: " + e.getMessage());
        }
    }

    public static class PrescriptionRecord {
        public String date;
        public String medicine;
        public String dosage;

        public PrescriptionRecord(String date, String medicine, String dosage) {
            this.date = date;
            this.medicine = medicine;
            this.dosage = dosage;
        }
    }
}
