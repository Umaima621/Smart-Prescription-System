package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SendPrescriptionView extends VBox {

    private ComboBox<String> patientCombo;
    private TextField medicineField;
    private ComboBox<String> dosageCombo;
    private TextArea instructionsArea;
    private Label statusLabel;
    private String doctorId;

    public SendPrescriptionView(String doctorId) {
        this.doctorId = doctorId;
        setSpacing(0);
        setPadding(new Insets(0));
        buildUI();
        loadPatients();
    }

    private void buildUI() {
        VBox header = new VBox(8);
        header.setPadding(new Insets(UITheme.PADDING_LARGE));
        header.setStyle(UITheme.getMainContainerStyle());

        Label title = new Label("Send Prescription to Patient");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());

        Label subtitle = new Label("Select a patient and send them a prescription directly");
        subtitle.setFont(UITheme.fontSmall());
        subtitle.setTextFill(UITheme.colorTextMuted());

        header.getChildren().addAll(title, subtitle);

        VBox card = new VBox(UITheme.GAP_MEDIUM);
        card.setPadding(new Insets(UITheme.PADDING_LARGE));
        card.setStyle(UITheme.getCardStyle());
        card.setMaxWidth(650);

        GridPane grid = new GridPane();
        grid.setHgap(UITheme.GAP_MEDIUM);
        grid.setVgap(UITheme.GAP_MEDIUM);

        Label patientLbl = new Label("Select Patient:");
        patientLbl.setFont(UITheme.fontLabel());
        patientLbl.setStyle(UITheme.getLabelStyle());
        patientCombo = new ComboBox<>();
        patientCombo.setPromptText("Choose a patient...");
        patientCombo.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        patientCombo.setMaxWidth(Double.MAX_VALUE);
        grid.add(patientLbl, 0, 0);
        grid.add(patientCombo, 1, 0);

        Label medLbl = new Label("Medicine:");
        medLbl.setFont(UITheme.fontLabel());
        medLbl.setStyle(UITheme.getLabelStyle());
        medicineField = new TextField();
        medicineField.setPromptText("Enter medicine name...");
        medicineField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        medicineField.setStyle(UITheme.getTextFieldStyle());
        grid.add(medLbl, 0, 1);
        grid.add(medicineField, 1, 1);

        Label dosageLbl = new Label("Dosage Frequency:");
        dosageLbl.setFont(UITheme.fontLabel());
        dosageLbl.setStyle(UITheme.getLabelStyle());
        dosageCombo = new ComboBox<>();
        dosageCombo.getItems().addAll(
                "Once Daily", "Twice Daily",
                "Every 8 Hours", "Every 12 Hours", "As Needed");
        dosageCombo.setPromptText("Select frequency...");
        dosageCombo.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        dosageCombo.setMaxWidth(Double.MAX_VALUE);
        grid.add(dosageLbl, 0, 2);
        grid.add(dosageCombo, 1, 2);

        Label instLbl = new Label("Instructions:");
        instLbl.setFont(UITheme.fontLabel());
        instLbl.setStyle(UITheme.getLabelStyle());
        instructionsArea = new TextArea();
        instructionsArea.setPromptText("Special instructions for the patient...");
        instructionsArea.setPrefHeight(80);
        instructionsArea.setStyle(UITheme.getTextFieldStyle());
        grid.add(instLbl, 0, 3);
        grid.add(instructionsArea, 1, 3);

        card.getChildren().addAll(grid, new Separator());

        HBox btnBox = new HBox(UITheme.GAP_MEDIUM);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button sendBtn = new Button("Send Prescription");
        sendBtn.setStyle(UITheme.getPrimaryButtonStyle());
        sendBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        sendBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        sendBtn.setOnAction(e -> sendPrescription());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(UITheme.getSecondaryButtonStyle());
        clearBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        clearBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        clearBtn.setOnAction(e -> clearForm());

        btnBox.getChildren().addAll(clearBtn, sendBtn);
        card.getChildren().add(btnBox);

        VBox tableCard = new VBox(UITheme.GAP_MEDIUM);
        tableCard.setPadding(new Insets(UITheme.PADDING_LARGE));
        tableCard.setStyle(UITheme.getCardStyle());

        Label tableTitle = new Label("Recently Sent Prescriptions");
        tableTitle.setFont(UITheme.fontHeading4());
        tableTitle.setTextFill(UITheme.colorTextPrimary());

        TableView<SentPrescription> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(200);
        table.setStyle("-fx-border-color: " + UITheme.BORDER_COLOR + ";");

        TableColumn<SentPrescription, String> patCol = new TableColumn<>("Patient");
        patCol.setCellValueFactory(c -> new javafx.beans.property
                .SimpleStringProperty(c.getValue().patientName));

        TableColumn<SentPrescription, String> medCol = new TableColumn<>("Medicine");
        medCol.setCellValueFactory(c -> new javafx.beans.property
                .SimpleStringProperty(c.getValue().medicine));

        TableColumn<SentPrescription, String> dosCol = new TableColumn<>("Dosage");
        dosCol.setCellValueFactory(c -> new javafx.beans.property
                .SimpleStringProperty(c.getValue().dosage));

        TableColumn<SentPrescription, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new javafx.beans.property
                .SimpleStringProperty(c.getValue().date));

        table.getColumns().addAll(patCol, medCol, dosCol, dateCol);
        table.setPlaceholder(new Label("No prescriptions sent yet."));

        loadSentPrescriptions(table);

        tableCard.getChildren().addAll(tableTitle, new Separator(), table);

        statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontSmall());
        statusLabel.setPadding(new Insets(UITheme.PADDING_SMALL));

        sendBtn.setOnAction(e -> {
            sendPrescription();
            loadSentPrescriptions(table);
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

    private void loadPatients() {
        patientCombo.getItems().clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT userID, name FROM Users WHERE role = 'Patient'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patientCombo.getItems().add(
                        rs.getString("userID") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading patients: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void loadSentPrescriptions(TableView<SentPrescription> table) {
        table.getItems().clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT u.name, p.medicineName, p.dosageFrequency, p.prescribedDate " +
                        "FROM Prescriptions p " +
                        "JOIN Users u ON p.patientID = u.email " + 
                        "WHERE p.doctorID = ? " +
                        "ORDER BY p.prescribedDate DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                table.getItems().add(new SentPrescription(
                        rs.getString("name"),
                        rs.getString("medicineName"),
                        rs.getString("dosageFrequency"),
                        rs.getString("prescribedDate")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading prescriptions: " + e.getMessage());
        }
    }

    private void sendPrescription() {
        String selectedPatient = patientCombo.getValue();
        String medicine = medicineField.getText().trim();
        String dosage = dosageCombo.getValue();
        String instructions = instructionsArea.getText().trim();

        if (selectedPatient == null || medicine.isEmpty() || dosage == null) {
            statusLabel.setText("Please fill in all required fields.");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }

        String patientID = selectedPatient.split(" - ")[0];

     String patientEmail = patientID;
     try {
         Connection tempConn = DatabaseConnection.getInstance().getConnection();
         String emailSql = "SELECT email FROM Users WHERE userID = ?";
         PreparedStatement emailStmt = tempConn.prepareStatement(emailSql);
         emailStmt.setString(1, patientID);
         ResultSet emailRs = emailStmt.executeQuery();
         if (emailRs.next()) {
             patientEmail = emailRs.getString("email");
         }
     } catch (SQLException ex) {
         System.out.println("Email lookup failed: " + ex.getMessage());
     }

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO Prescriptions " +
                    "(doctorID, patientID, medicineName, dosageFrequency, instructions, prescribedDate) " +
                    "VALUES (?, ?, ?, ?, ?, GETDATE())";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, doctorId);
        stmt.setString(2, patientEmail);
        stmt.setString(3, medicine);
        stmt.setString(4, dosage);
        stmt.setString(5, instructions);
        stmt.executeUpdate();

        String sql2 = "INSERT INTO Medicines (userID, medicineName, dosage, frequency, startDate, endDate) " +
                     "VALUES (?, ?, ?, ?, CAST(GETDATE() AS DATE), DATEADD(month, 1, CAST(GETDATE() AS DATE)))";
        PreparedStatement stmt2 = conn.prepareStatement(sql2);
        stmt2.setString(1, patientEmail);
        stmt2.setString(2, medicine);
        stmt2.setString(3, "As prescribed");
        stmt2.setString(4, dosage);
        stmt2.executeUpdate();

        statusLabel.setText("✓ Prescription sent to " + selectedPatient.split(" - ")[1]);
        statusLabel.setStyle(UITheme.getSuccessStyle());
        NotificationPopup.show(MainApp.mainStage, "Prescription sent successfully!", NotificationPopup.Type.SUCCESS);
        clearForm();

        } catch (SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void clearForm() {
        patientCombo.setValue(null);
        medicineField.clear();
        dosageCombo.setValue(null);
        instructionsArea.clear();
    }

    public static class SentPrescription {
        public String patientName, medicine, dosage, date;

        public SentPrescription(String patientName, String medicine,
                String dosage, String date) {
            this.patientName = patientName;
            this.medicine = medicine;
            this.dosage = dosage;
            this.date = date;
        }
    }
}