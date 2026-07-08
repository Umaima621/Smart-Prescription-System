package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PrescribeMedicineView extends VBox {
	
    private String currentDoctorId;
    private TextField patientIdField;
    private TextField medicineNameField;
    private ComboBox<String> dosageCombo;
    private TextArea instructionsArea;
    private Label statusLabel;

    public PrescribeMedicineView(String doctorId) {
        this.currentDoctorId = doctorId;
        setSpacing(0);
        setPadding(new Insets(0));
        buildUI();
    }

    private void buildUI() {
        VBox header = new VBox(12);
        header.setPadding(new Insets(UITheme.PADDING_LARGE));
        header.setStyle(UITheme.getMainContainerStyle());

        Label title = new Label("Create New Prescription");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());

        header.getChildren().add(title);

        VBox card = new VBox(UITheme.GAP_MEDIUM);
        card.setPadding(new Insets(UITheme.PADDING_LARGE));
        card.setStyle(UITheme.getCardStyle());
        card.setMaxWidth(600);

        GridPane grid = new GridPane();
        grid.setHgap(UITheme.GAP_MEDIUM);
        grid.setVgap(UITheme.GAP_MEDIUM);

        Label patientLbl = new Label("Patient ID:");
        patientLbl.setFont(UITheme.fontLabel());
        patientLbl.setStyle(UITheme.getLabelStyle());
        patientIdField = buildTextField("e.g., PT-9920");
        grid.add(patientLbl, 0, 0);
        grid.add(patientIdField, 1, 0);

        Label medLbl = new Label("Medication:");
        medLbl.setFont(UITheme.fontLabel());
        medLbl.setStyle(UITheme.getLabelStyle());
        medicineNameField = buildTextField("Enter medicine name...");
        grid.add(medLbl, 0, 1);
        grid.add(medicineNameField, 1, 1);

        Label dosageLbl = new Label("Dosage Frequency:");
        dosageLbl.setFont(UITheme.fontLabel());
        dosageLbl.setStyle(UITheme.getLabelStyle());
        dosageCombo = new ComboBox<>();
        dosageCombo.getItems().addAll("Once Daily", "Twice Daily", "Every 8 Hours", "Every 12 Hours", "As Needed");
        dosageCombo.setPrefHeight(UITheme.FIELD_MIN_HEIGHT);
        grid.add(dosageLbl, 0, 2);
        grid.add(dosageCombo, 1, 2);

        Label instLbl = new Label("Instructions:");
        instLbl.setFont(UITheme.fontLabel());
        instLbl.setStyle(UITheme.getLabelStyle());
        instructionsArea = new TextArea();
        instructionsArea.setPromptText("Special instructions for the patient...");
        instructionsArea.setPrefHeight(100);
        instructionsArea.setStyle(UITheme.getTextFieldStyle());
        grid.add(instLbl, 0, 3);
        grid.add(instructionsArea, 1, 3);

        card.getChildren().addAll(grid, new Separator());

        HBox buttonBox = new HBox(UITheme.GAP_MEDIUM);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button submitBtn = new Button("Issue Prescription");
        submitBtn.setStyle(UITheme.getPrimaryButtonStyle());
        submitBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        submitBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        submitBtn.setOnAction(e -> handleSubmit());

        Button resetBtn = new Button("Clear");
        resetBtn.setStyle(UITheme.getSecondaryButtonStyle());
        resetBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        resetBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        resetBtn.setOnAction(e -> clearForm());

        buttonBox.getChildren().addAll(resetBtn, submitBtn);
        card.getChildren().add(buttonBox);

        statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontSmall());

        ScrollPane scrollPane = new ScrollPane(card);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(UITheme.getMainContainerStyle());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane, statusLabel);
    }

    private TextField buildTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        tf.setStyle(UITheme.getTextFieldStyle());
        return tf;
    }

    private void handleSubmit() {
    	String patientId = patientIdField.getText().trim();
    	try {
    	    Connection conn2 = db.DatabaseConnection.getInstance().getConnection();
    	    String lookupSql = "SELECT email FROM Users WHERE email = ? OR name LIKE ? OR CAST(userID AS VARCHAR) = ?";
    	    PreparedStatement lookupStmt = conn2.prepareStatement(lookupSql);
    	    lookupStmt.setString(1, patientId);
    	    lookupStmt.setString(2, "%" + patientId + "%");
    	    lookupStmt.setString(3, patientId);
    	    ResultSet lookupRs = lookupStmt.executeQuery();
    	    if (lookupRs.next()) {
    	        patientId = lookupRs.getString("email");
    	    }
    	} catch (java.sql.SQLException ex) {
    	    System.out.println("Patient lookup: " + ex.getMessage());
    	}
        String medicine = medicineNameField.getText().trim();
        String dosage = dosageCombo.getValue();
        String instructions = instructionsArea.getText().trim();

        if (patientId.isEmpty() || medicine.isEmpty() || dosage == null) {
            statusLabel.setText("Please fill in all required fields");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }

        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO Prescriptions (doctorID, patientID, medicineName, dosageFrequency, instructions, prescribedDate) VALUES (?, ?, ?, ?, ?, GETDATE())";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentDoctorId);
            stmt.setString(2, patientId);
            stmt.setString(3, medicine);
            stmt.setString(4, dosage);
            stmt.setString(5, instructions);
            stmt.executeUpdate();

         try {
             String sql2 = "INSERT INTO Medicines (userID, medicineName, dosage, frequency, startDate, endDate) " +
                          "VALUES (?, ?, ?, ?, CAST(GETDATE() AS DATE), DATEADD(month, 1, CAST(GETDATE() AS DATE)))";
             PreparedStatement stmt2 = conn.prepareStatement(sql2);
             stmt2.setString(1, patientId);
             stmt2.setString(2, medicine);
             stmt2.setString(3, "As prescribed");
             stmt2.setString(4, dosage);
             stmt2.executeUpdate();
         } catch (SQLException ex) {
             System.out.println("Could not add to medicines: " + ex.getMessage());
         }

         statusLabel.setText("✓ Prescription issued successfully to " + patientId);
         statusLabel.setStyle(UITheme.getSuccessStyle());
         NotificationPopup.show(MainApp.mainStage, "Prescription issued successfully!", NotificationPopup.Type.SUCCESS);
         clearForm();

        } catch (java.sql.SQLException e) {
            statusLabel.setText("Error saving: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void clearForm() {
        patientIdField.clear();
        medicineNameField.clear();
        dosageCombo.setValue(null);
        instructionsArea.clear();
        statusLabel.setText("");
    }
}
