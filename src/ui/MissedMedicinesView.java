package ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MissedMedicinesView extends VBox {

    private TableView<MissedMedicineRecord> table;

    public MissedMedicinesView() {
        setSpacing(0);
        setPadding(new Insets(0));
        buildUI();
        loadData();
    }

    private void buildUI() {
        VBox header = new VBox(12);
        header.setPadding(new Insets(UITheme.PADDING_LARGE));
        header.setStyle(UITheme.getMainContainerStyle());

        Label title = new Label("Medicine Adherence Tracking");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());

        Label subtitle = new Label("View medicines flagged as missed by patients");
        subtitle.setFont(UITheme.fontSmall());
        subtitle.setTextFill(UITheme.colorTextMuted());

        header.getChildren().addAll(title, subtitle);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 10; "
                     + "-fx-border-color: " + UITheme.BORDER_COLOR + "; "
                     + "-fx-border-width: 1;");

        TableColumn<MissedMedicineRecord, String> timeCol = new TableColumn<>("Time Missed");
        timeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().timeMissed));
        
        TableColumn<MissedMedicineRecord, String> patientCol = new TableColumn<>("Patient Name");
        patientCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().patientName));
        
        TableColumn<MissedMedicineRecord, String> medCol = new TableColumn<>("Medicine");
        medCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().medicine));
        
        TableColumn<MissedMedicineRecord, String> noteCol = new TableColumn<>("Patient Note");
        noteCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().note));

        table.getColumns().add(timeCol);
        table.getColumns().add(patientCol);
        table.getColumns().add(medCol);
        table.getColumns().add(noteCol);
        table.setPlaceholder(new Label("No missed doses reported."));

        VBox.setVgrow(table, Priority.ALWAYS);

        getChildren().addAll(header, table);
    }

    private void loadData() {
        table.getItems().clear();
        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT u.name, m.medicineName, mi.scheduled_time " +
                        "FROM medicine_intake mi " +
                        "LEFT JOIN Users u ON mi.patient_id = u.email " +
                        "LEFT JOIN Medicines m ON CAST(mi.medicine_id AS VARCHAR) = CAST(m.medicineID AS VARCHAR) " +
                        "WHERE mi.is_taken = 0 " +
                        "AND mi.scheduled_time < GETDATE() " +
                        "ORDER BY mi.scheduled_time DESC";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String patientName = rs.getString("name");
                String medicineName = rs.getString("medicineName");
                String scheduledTime = rs.getString("scheduled_time");

                table.getItems().add(new MissedMedicineRecord(
                        scheduledTime != null ? scheduledTime : "Unknown time",
                        patientName != null ? patientName : "Unknown patient",
                        medicineName != null ? medicineName : "Unknown medicine",
                        "Not taken"
                ));
            }

            if (table.getItems().isEmpty()) {
                table.setPlaceholder(new Label("No missed medicines found. ✓"));
            }

        } catch (java.sql.SQLException e) {
            table.setPlaceholder(new Label("Error loading: " + e.getMessage()));
            System.out.println("Missed medicines error: " + e.getMessage());
        }
    }

    public static class MissedMedicineRecord {
        public String timeMissed;
        public String patientName;
        public String medicine;
        public String note;

        public MissedMedicineRecord(String timeMissed, String patientName, String medicine, String note) {
            this.timeMissed = timeMissed;
            this.patientName = patientName;
            this.medicine = medicine;
            this.note = note;
        }
    }
}
