package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import db.DatabaseConnection;

public class ScheduleReminderView extends VBox {

    private ComboBox<String> medicineCombo;
    private TextField timeField;
    private DatePicker datePicker;
    private Label statusLabel;
    private String patientId;

    public ScheduleReminderView(String patientId) {
        this.patientId = patientId;
        setSpacing(0);
        setPadding(new Insets(0));
        buildUI();
        loadMedicines();
    }

    private void buildUI() {
        VBox header = new VBox(8);
        header.setPadding(new Insets(UITheme.PADDING_LARGE));
        header.setStyle(UITheme.getMainContainerStyle());

        Label title = new Label("Schedule Reminder");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());

        Label subtitle = new Label("Set a reminder for your medicines");
        subtitle.setFont(UITheme.fontSmall());
        subtitle.setTextFill(UITheme.colorTextMuted());

        header.getChildren().addAll(title, subtitle);

        VBox card = new VBox(UITheme.GAP_MEDIUM);
        card.setPadding(new Insets(UITheme.PADDING_LARGE));
        card.setStyle(UITheme.getCardStyle());
        card.setMaxWidth(600);

        GridPane grid = new GridPane();
        grid.setHgap(UITheme.GAP_MEDIUM);
        grid.setVgap(UITheme.GAP_MEDIUM);

        Label medLbl = new Label("Select Medicine:");
        medLbl.setFont(UITheme.fontLabel());
        medLbl.setStyle(UITheme.getLabelStyle());
        medicineCombo = new ComboBox<>();
        medicineCombo.setPromptText("Choose a medicine...");
        medicineCombo.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        medicineCombo.setMaxWidth(Double.MAX_VALUE);
        grid.add(medLbl, 0, 0);
        grid.add(medicineCombo, 1, 0);

        Label timeLbl = new Label("Reminder Time:");
        timeLbl.setFont(UITheme.fontLabel());
        timeLbl.setStyle(UITheme.getLabelStyle());
        timeField = new TextField();
        timeField.setPromptText("e.g. 08:00");
        timeField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        timeField.setStyle(UITheme.getTextFieldStyle());
        grid.add(timeLbl, 0, 1);
        grid.add(timeField, 1, 1);

        Label dateLbl = new Label("Reminder Date:");
        dateLbl.setFont(UITheme.fontLabel());
        dateLbl.setStyle(UITheme.getLabelStyle());
        datePicker = new DatePicker();
        datePicker.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        grid.add(dateLbl, 0, 2);
        grid.add(datePicker, 1, 2);

        Label infoLbl = new Label("ℹ Time format: HH:MM (24 hour) e.g. 08:00 or 14:30");
        infoLbl.setFont(UITheme.fontSmall());
        infoLbl.setTextFill(UITheme.colorTextMuted());

        card.getChildren().addAll(grid, infoLbl, new Separator());

        HBox btnBox = new HBox(UITheme.GAP_MEDIUM);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button("Set Reminder");
        saveBtn.setStyle(UITheme.getPrimaryButtonStyle());
        saveBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        saveBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        saveBtn.setOnAction(e -> saveReminder());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(UITheme.getSecondaryButtonStyle());
        clearBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        clearBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        clearBtn.setOnAction(e -> clearForm());

        btnBox.getChildren().addAll(clearBtn, saveBtn);
        card.getChildren().add(btnBox);

        VBox tableCard = new VBox(UITheme.GAP_MEDIUM);
        tableCard.setPadding(new Insets(UITheme.PADDING_LARGE));
        tableCard.setStyle(UITheme.getCardStyle());

        Label tableTitle = new Label("Your Scheduled Reminders");
        tableTitle.setFont(UITheme.fontHeading4());
        tableTitle.setTextFill(UITheme.colorTextPrimary());

        TableView<ReminderRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        table.setStyle("-fx-border-color: " + UITheme.BORDER_COLOR + ";");

        TableColumn<ReminderRecord, String> medCol = new TableColumn<>("Medicine");
        medCol.setCellValueFactory(c -> new javafx.beans.property
                .SimpleStringProperty(c.getValue().medicineName));

        TableColumn<ReminderRecord, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(c -> new javafx.beans.property
                .SimpleStringProperty(c.getValue().time));

        TableColumn<ReminderRecord, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new javafx.beans.property
                .SimpleStringProperty(c.getValue().date));

        table.getColumns().addAll(medCol, timeCol, dateCol);
        table.setPlaceholder(new Label("No reminders set yet."));

        loadReminders(table);

        tableCard.getChildren().addAll(tableTitle, new Separator(), table);

        statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontSmall());
        statusLabel.setPadding(new Insets(UITheme.PADDING_SMALL));

        saveBtn.setOnAction(e -> {
            saveReminder();
            loadReminders(table);
        });

        VBox content = new VBox(UITheme.GAP_LARGE);
        content.setPadding(new Insets(UITheme.PADDING_MEDIUM));
        content.setStyle(UITheme.getMainContainerStyle());
        content.getChildren().addAll(card, tableCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(UITheme.getMainContainerStyle());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane, statusLabel);
    }

    private void loadMedicines() {
        medicineCombo.getItems().clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT medicineID, medicineName FROM Medicines WHERE userID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                medicineCombo.getItems().add(
                        rs.getInt("medicineID") + " - " + rs.getString("medicineName"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading medicines: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void loadReminders(TableView<ReminderRecord> table) {
        table.getItems().clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT r.reminderID, m.medicineName, r.reminderTime, r.reminderDate " +
                        "FROM Reminders r JOIN Medicines m ON r.medicineID = m.medicineID " +
                        "WHERE r.userID = ? ORDER BY r.reminderDate, r.reminderTime";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                table.getItems().add(new ReminderRecord(
                        rs.getInt("reminderID"),
                        rs.getString("medicineName"),
                        rs.getString("reminderTime"),
                        rs.getString("reminderDate")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading reminders: " + e.getMessage());
        }
    }

    private void saveReminder() {
        String selectedMedicine = medicineCombo.getValue();
        String time = timeField.getText().trim();

        if (selectedMedicine == null || time.isEmpty() || datePicker.getValue() == null) {
            statusLabel.setText("Please fill in all fields.");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }

        if (!time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            statusLabel.setText("Invalid time format. Use HH:MM e.g. 08:00");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }

        int medicineID = Integer.parseInt(selectedMedicine.split(" - ")[0]);

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO Reminders (medicineID, userID, reminderTime, reminderDate) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, medicineID);
            stmt.setString(2, patientId);
            stmt.setString(3, time);
            stmt.setDate(4, java.sql.Date.valueOf(datePicker.getValue()));
            stmt.executeUpdate();

            statusLabel.setText("✓ Reminder set successfully!");
            statusLabel.setStyle(UITheme.getSuccessStyle());
            NotificationPopup.show(MainApp.mainStage, "Reminder set successfully!", NotificationPopup.Type.SUCCESS);
            clearForm();

        } catch (SQLException e) {
            statusLabel.setText("Error saving: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void clearForm() {
        medicineCombo.setValue(null);
        timeField.clear();
        datePicker.setValue(null);
        statusLabel.setText("");
    }

    public static class ReminderRecord {
        public int id;
        public String medicineName, time, date;

        public ReminderRecord(int id, String medicineName, String time, String date) {
            this.id = id;
            this.medicineName = medicineName;
            this.time = time;
            this.date = date;
        }
    }
}