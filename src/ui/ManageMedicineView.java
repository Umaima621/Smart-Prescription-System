package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import db.DatabaseConnection;

public class ManageMedicineView extends VBox {

    private TableView<MedicineRecord> table;
    private TextField medicineNameField;
    private TextField dosageField;
    private TextField frequencyField;
    private Label statusLabel;
    private String patientId;
    private int selectedMedicineID = -1;

    public ManageMedicineView(String patientId) {
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

        Label title = new Label("Manage Medicines");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());

        Label subtitle = new Label("View, edit or delete your medicines");
        subtitle.setFont(UITheme.fontSmall());
        subtitle.setTextFill(UITheme.colorTextMuted());

        header.getChildren().addAll(title, subtitle);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 10; -fx-border-color: "
                + UITheme.BORDER_COLOR + "; -fx-border-width: 1;");
        table.setPrefHeight(250);

        TableColumn<MedicineRecord, String> nameCol = new TableColumn<>("Medicine");
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().name));

        TableColumn<MedicineRecord, String> dosageCol = new TableColumn<>("Dosage");
        dosageCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().dosage));

        TableColumn<MedicineRecord, String> freqCol = new TableColumn<>("Frequency");
        freqCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().frequency));

        TableColumn<MedicineRecord, String> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().startDate));

        TableColumn<MedicineRecord, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().endDate));

        table.getColumns().addAll(nameCol, dosageCol, freqCol, startCol, endCol);
        table.setPlaceholder(new Label("No medicines found."));

        table.setOnMouseClicked(e -> {
            MedicineRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedMedicineID = selected.id;
                medicineNameField.setText(selected.name);
                dosageField.setText(selected.dosage);
                frequencyField.setText(selected.frequency);
            }
        });

        VBox card = new VBox(UITheme.GAP_MEDIUM);
        card.setPadding(new Insets(UITheme.PADDING_LARGE));
        card.setStyle(UITheme.getCardStyle());
        card.setMaxWidth(600);

        Label formTitle = new Label("Edit Selected Medicine");
        formTitle.setFont(UITheme.fontHeading4());
        formTitle.setTextFill(UITheme.colorTextPrimary());

        GridPane grid = new GridPane();
        grid.setHgap(UITheme.GAP_MEDIUM);
        grid.setVgap(UITheme.GAP_MEDIUM);

        Label nameLbl = new Label("Medicine Name:");
        nameLbl.setFont(UITheme.fontLabel());
        nameLbl.setStyle(UITheme.getLabelStyle());
        medicineNameField = new TextField();
        medicineNameField.setPromptText("Medicine name");
        medicineNameField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        medicineNameField.setStyle(UITheme.getTextFieldStyle());
        grid.add(nameLbl, 0, 0);
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

        card.getChildren().addAll(formTitle, new Separator(), grid);

        HBox btnBox = new HBox(UITheme.GAP_MEDIUM);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle(UITheme.getPrimaryButtonStyle());
        updateBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        updateBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        updateBtn.setOnAction(e -> updateMedicine());

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #A02020; -fx-text-fill: white; "
                + "-fx-background-radius: 8; -fx-padding: 12 30; -fx-cursor: hand;");
        deleteBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        deleteBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        deleteBtn.setOnAction(e -> deleteMedicine());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(UITheme.getSecondaryButtonStyle());
        clearBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        clearBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        clearBtn.setOnAction(e -> clearForm());

        btnBox.getChildren().addAll(clearBtn, deleteBtn, updateBtn);
        card.getChildren().add(btnBox);

        statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontSmall());
        statusLabel.setPadding(new Insets(UITheme.PADDING_SMALL));

        VBox content = new VBox(UITheme.GAP_LARGE);
        content.setPadding(new Insets(UITheme.PADDING_MEDIUM));
        content.setStyle(UITheme.getMainContainerStyle());
        content.getChildren().addAll(table, card);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(UITheme.getMainContainerStyle());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane, statusLabel);
    }

    private void loadMedicines() {
        table.getItems().clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT * FROM Medicines WHERE userID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                table.getItems().add(new MedicineRecord(
                        rs.getInt("medicineID"),
                        rs.getString("medicineName"),
                        rs.getString("dosage"),
                        rs.getString("frequency"),
                        rs.getString("startDate"),
                        rs.getString("endDate")
                ));
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void updateMedicine() {
        if (selectedMedicineID == -1) {
            statusLabel.setText("Please select a medicine from the table first.");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "UPDATE Medicines SET medicineName=?, dosage=?, frequency=? WHERE medicineID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, medicineNameField.getText().trim());
            stmt.setString(2, dosageField.getText().trim());
            stmt.setString(3, frequencyField.getText().trim());
            stmt.setInt(4, selectedMedicineID);
            stmt.executeUpdate();
            statusLabel.setText("✓ Medicine updated successfully!");
            statusLabel.setStyle(UITheme.getSuccessStyle());
            NotificationPopup.show(MainApp.mainStage, "Medicine updated successfully!", NotificationPopup.Type.SUCCESS);
            loadMedicines();
            clearForm();
        } catch (SQLException e) {
            statusLabel.setText("Error updating: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void deleteMedicine() {
        if (selectedMedicineID == -1) {
            statusLabel.setText("Please select a medicine to delete.");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Medicine");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this medicine?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Connection conn = DatabaseConnection.getInstance().getConnection();
                    String sql = "DELETE FROM Medicines WHERE medicineID=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, selectedMedicineID);
                    stmt.executeUpdate();
                    statusLabel.setText("✓ Medicine deleted.");
                    statusLabel.setStyle(UITheme.getSuccessStyle());
                    NotificationPopup.show(MainApp.mainStage, "Medicine deleted successfully!", NotificationPopup.Type.SUCCESS);
                    loadMedicines();
                    clearForm();
                } catch (SQLException e) {
                    statusLabel.setText("Error deleting: " + e.getMessage());
                    statusLabel.setStyle(UITheme.getErrorStyle());
                }
            }
        });
    }

    private void clearForm() {
        medicineNameField.clear();
        dosageField.clear();
        frequencyField.clear();
        selectedMedicineID = -1;
    }

    public static class MedicineRecord {
        public int id;
        public String name, dosage, frequency, startDate, endDate;

        public MedicineRecord(int id, String name, String dosage,
                String frequency, String startDate, String endDate) {
            this.id = id;
            this.name = name;
            this.dosage = dosage;
            this.frequency = frequency;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}