package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReminderNotificationService {

    private ScheduledExecutorService scheduler;
    private String patientId;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public ReminderNotificationService(String patientId) {
        this.patientId = patientId;
    }

    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            checkReminders();
        }, 0, 60, TimeUnit.SECONDS);
        System.out.println("[ReminderService] Started for patient: " + patientId);
    }

    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("[ReminderService] Stopped.");
        }
    }

    private void checkReminders() {
        String currentTime = LocalTime.now().format(TIME_FMT);
        String currentDate = LocalDate.now().toString();

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT r.reminderID, m.medicineName, m.dosage, r.reminderTime " +
                        "FROM Reminders r " +
                        "JOIN Medicines m ON r.medicineID = m.medicineID " +
                        "WHERE r.userID = ? " +
                        "AND r.reminderDate = ? " +
                        "AND r.reminderTime LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            stmt.setString(2, currentDate);
            stmt.setString(3, currentTime + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String medicineName = rs.getString("medicineName");
                String dosage = rs.getString("dosage");
                String reminderTime = rs.getString("reminderTime");
                int reminderID = rs.getInt("reminderID");

                Platform.runLater(() -> {
                    showNotificationPopup(medicineName, dosage, reminderTime, reminderID);
                });
            }
        } catch (SQLException e) {
            System.err.println("[ReminderService] DB error: " + e.getMessage());
        }
    }

    private void showNotificationPopup(String medicineName, String dosage,
                                        String time, int reminderID) {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setAlwaysOnTop(true);

        VBox root = new VBox(16);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white; " +
                "-fx-border-color: " + UITheme.TEAL_PRIMARY + "; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 12; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");
        root.setMaxWidth(380);

        Label bell = new Label("🔔");
        bell.setFont(UITheme.fontHeading1());

        Label title = new Label("Medicine Reminder!");
        title.setFont(UITheme.fontHeading4());
        title.setTextFill(UITheme.colorTextPrimary());

        VBox infoBox = new VBox(6);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setPadding(new Insets(12));
        infoBox.setStyle("-fx-background-color: #f0f7f5; " +
                "-fx-background-radius: 8;");

        Label medName = new Label(medicineName);
        medName.setFont(UITheme.fontHeading4());
        medName.setTextFill(UITheme.colorTealPrimary());

        Label dosageLabel = new Label("Dosage: " + (dosage != null ? dosage : "As prescribed"));
        dosageLabel.setFont(UITheme.fontBody());
        dosageLabel.setTextFill(UITheme.colorTextMuted());

        Label timeLabel = new Label("Scheduled: " + time);
        timeLabel.setFont(UITheme.fontSmall());
        timeLabel.setTextFill(UITheme.colorTextMuted());

        infoBox.getChildren().addAll(medName, dosageLabel, timeLabel);

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER);

        Button takenBtn = new Button("✓ Mark as Taken");
        takenBtn.setStyle(UITheme.getPrimaryButtonStyle());
        takenBtn.setOnAction(e -> {
            markAsTaken(reminderID);
            popup.close();
        });

        Button dismissBtn = new Button("✗ Mark as Missed");
        dismissBtn.setPrefWidth(160);
        dismissBtn.setPrefHeight(42);
        dismissBtn.setStyle("-fx-background-color: #A02020; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16 8 16;");
        dismissBtn.setOnAction(e -> {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                String sql = "INSERT INTO medicine_intake " +
                            "(intake_id, medicine_id, patient_id, scheduled_time, is_taken) " +
                            "VALUES (?, ?, ?, GETDATE(), 0)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, java.util.UUID.randomUUID().toString());
                stmt.setString(2, String.valueOf(reminderID));
                stmt.setString(3, patientId);
                stmt.executeUpdate();
                System.out.println("[ReminderService] Marked as missed: reminder " + reminderID);
            } catch (SQLException ex) {
                System.err.println("[ReminderService] Error marking missed: " + ex.getMessage());
            }
            popup.close();
        });

        btnBox.getChildren().addAll(takenBtn, dismissBtn);

        root.getChildren().addAll(bell, title, infoBox, btnBox);

        Scene scene = new Scene(root);
        popup.setScene(scene);

        javafx.geometry.Rectangle2D screen =
                javafx.stage.Screen.getPrimary().getVisualBounds();
        popup.setX(screen.getMaxX() - 420);
        popup.setY(screen.getMaxY() - 300);

        popup.show();

        ScheduledExecutorService autoClose = Executors.newSingleThreadScheduledExecutor();
        autoClose.schedule(() -> {
            Platform.runLater(popup::close);
            autoClose.shutdown();
        }, 30, TimeUnit.SECONDS);
    }

    private void markAsTaken(int reminderID) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String sql = "INSERT INTO medicine_intake " +
                        "(intake_id, medicine_id, patient_id, scheduled_time, is_taken, taken_at) " +
                        "VALUES (?, ?, ?, GETDATE(), 1, GETDATE())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, java.util.UUID.randomUUID().toString());
            stmt.setString(2, String.valueOf(reminderID));
            stmt.setString(3, patientId);
            stmt.executeUpdate();
            System.out.println("[ReminderService] Marked as taken: reminder " + reminderID);
        } catch (SQLException e) {
            System.err.println("[ReminderService] Error marking taken: " + e.getMessage());
        }
    }
}