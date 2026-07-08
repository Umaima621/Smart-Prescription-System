package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import db.DatabaseConnection;

public class AddPrescriptionView extends VBox {

    private TextField medicineNameField;
    private TextField dosageField;
    private TextField frequencyField;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Label statusLabel;
    private String patientId;

    public AddPrescriptionView(String patientId) {
        this.patientId = patientId;
        setSpacing(0);
        setPadding(new Insets(0));
        buildUI();
    }

    private void buildUI() {
        VBox header = new VBox(8);
        header.setPadding(new Insets(UITheme.PADDING_LARGE));
        header.setStyle(UITheme.getMainContainerStyle());

        Label title = new Label("Add Prescription");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());

        Label subtitle = new Label("Add a new medicine prescription to your profile");
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

        Label medLbl = new Label("Medicine Name:");
        medLbl.setFont(UITheme.fontLabel());
        medLbl.setStyle(UITheme.getLabelStyle());
        medicineNameField = new TextField();
        medicineNameField.setPromptText("e.g. Paracetamol");
        medicineNameField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        medicineNameField.setStyle(UITheme.getTextFieldStyle());
        grid.add(medLbl, 0, 0);
        grid.add(medicineNameField, 1, 0);

        Label dosageLbl = new Label("Dosage:");
        dosageLbl.setFont(UITheme.fontLabel());
        dosageLbl.setStyle(UITheme.getLabelStyle());
        dosageField = new TextField();
        dosageField.setPromptText("e.g. 500mg");
        dosageField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        dosageField.setStyle(UITheme.getTextFieldStyle());
        grid.add(dosageLbl, 0, 1);
        grid.add(dosageField, 1, 1);

        Label freqLbl = new Label("Frequency:");
        freqLbl.setFont(UITheme.fontLabel());
        freqLbl.setStyle(UITheme.getLabelStyle());
        frequencyField = new TextField();
        frequencyField.setPromptText("e.g. Twice Daily");
        frequencyField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        frequencyField.setStyle(UITheme.getTextFieldStyle());
        grid.add(freqLbl, 0, 2);
        grid.add(frequencyField, 1, 2);

        Label startLbl = new Label("Start Date:");
        startLbl.setFont(UITheme.fontLabel());
        startLbl.setStyle(UITheme.getLabelStyle());
        startDatePicker = new DatePicker();
        startDatePicker.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        grid.add(startLbl, 0, 3);
        grid.add(startDatePicker, 1, 3);

        Label endLbl = new Label("End Date:");
        endLbl.setFont(UITheme.fontLabel());
        endLbl.setStyle(UITheme.getLabelStyle());
        endDatePicker = new DatePicker();
        endDatePicker.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        grid.add(endLbl, 0, 4);
        grid.add(endDatePicker, 1, 4);

        card.getChildren().addAll(grid, new Separator());

        HBox btnBox = new HBox(UITheme.GAP_MEDIUM);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button saveBtn = new Button("Add Prescription");
        saveBtn.setStyle(UITheme.getPrimaryButtonStyle());
        saveBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        saveBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        saveBtn.setOnAction(e -> savePrescription());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(UITheme.getSecondaryButtonStyle());
        clearBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        clearBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        clearBtn.setOnAction(e -> clearForm());

        btnBox.getChildren().addAll(clearBtn, saveBtn);
        card.getChildren().add(btnBox);

        statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontSmall());
        statusLabel.setPadding(new Insets(UITheme.PADDING_SMALL));

        VBox content = new VBox(UITheme.GAP_LARGE);
        content.setPadding(new Insets(UITheme.PADDING_MEDIUM));
        content.setStyle(UITheme.getMainContainerStyle());
        content.getChildren().addAll(card);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(UITheme.getMainContainerStyle());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane, statusLabel);
    }

    private void savePrescription() {
        String medicine = medicineNameField.getText().trim();
        String dosage = dosageField.getText().trim();
        String frequency = frequencyField.getText().trim();

        if (medicine.isEmpty() || dosage.isEmpty() || frequency.isEmpty()
                || startDatePicker.getValue() == null
                || endDatePicker.getValue() == null) {
            statusLabel.setText("Please fill in all fields.");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO Medicines (userID, medicineName, dosage, frequency, startDate, endDate) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            stmt.setString(2, medicine);
            stmt.setString(3, dosage);
            stmt.setString(4, frequency);
            stmt.setDate(5, java.sql.Date.valueOf(startDatePicker.getValue()));
            stmt.setDate(6, java.sql.Date.valueOf(endDatePicker.getValue()));
            stmt.executeUpdate();

            statusLabel.setText("✓ Prescription added successfully!");
            statusLabel.setStyle(UITheme.getSuccessStyle());
            NotificationPopup.show(MainApp.mainStage, "Prescription added successfully!", NotificationPopup.Type.SUCCESS);
            clearForm();

        } catch (SQLException e) {
        	statusLabel.setText("Error saving: " + e.getMessage());
        	statusLabel.setStyle(UITheme.getErrorStyle());
        	NotificationPopup.show(MainApp.mainStage, "Error saving prescription!", NotificationPopup.Type.ERROR);
        }
    }

    private void clearForm() {
        medicineNameField.clear();
        dosageField.clear();
        frequencyField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        statusLabel.setText("");
    }
}