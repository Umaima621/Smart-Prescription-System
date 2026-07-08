package ui;

import controller.NotificationService;
import model.MedicineIntake;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DailyChecklistView extends VBox {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a");

    private final NotificationService notificationService;
    private final String              patientId;

    private VBox        checklistContainer;
    private Label       summaryLabel;

    public DailyChecklistView(String patientId) {
        this.patientId           = patientId;
        this.notificationService = new NotificationService();

        setSpacing(0);
        setPadding(new Insets(0));
        buildUI();
        loadChecklist();
    }

    private void buildUI() {
        HBox header = new HBox();
        header.setPadding(new Insets(16, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + UITheme.BG_SURFACE + "; "
                       + "-fx-border-color: " + UITheme.BORDER_COLOR + "; "
                       + "-fx-border-width: 0 0 1 0;");

        Label title = new Label("Today's Medicines");
        title.setFont(UITheme.fontHeading4());
        title.setTextFill(UITheme.colorTextPrimary());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        summaryLabel = new Label("");
        summaryLabel.setFont(UITheme.fontSmall());
        summaryLabel.setTextFill(UITheme.colorTextMuted());

        Button refreshBtn = new Button("↻ Refresh");
        refreshBtn.setStyle("-fx-background-color: " + UITheme.BG_PRIMARY + "; "
                           + "-fx-text-fill: " + UITheme.TEXT_PRIMARY + "; "
                           + "-fx-border-radius: 6; "
                           + "-fx-background-radius: 6; "
                           + "-fx-cursor: hand;");
        refreshBtn.setOnAction(e -> loadChecklist());

        header.getChildren().addAll(title, spacer, summaryLabel,
                                    new Label("  "), refreshBtn);

        checklistContainer = new VBox(8);
        checklistContainer.setPadding(new Insets(16, 20, 16, 20));
        checklistContainer.setStyle(UITheme.getMainContainerStyle());

        ScrollPane scrollPane = new ScrollPane(checklistContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(UITheme.getMainContainerStyle());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, scrollPane);
    }

    private void loadChecklist() {
        checklistContainer.getChildren().clear();

        List<MedicineIntake> intakes;
        try {
            intakes = notificationService.getTodayIntakes(patientId);
        } catch (RuntimeException ex) {
            showError("Unable to load checklist from database.\n" + ex.getMessage());
            summaryLabel.setText("DB unavailable");
            Label empty = new Label("Database unavailable. Please check SQL Server connection.");
            empty.setFont(UITheme.fontBody());
            empty.setTextFill(UITheme.colorTextMuted());
            empty.setPadding(new Insets(30));
            checklistContainer.getChildren().add(empty);
            return;
        }

        if (intakes.isEmpty()) {
            Label empty = new Label("No medicines scheduled for today.");
            empty.setFont(UITheme.fontBody());
            empty.setTextFill(UITheme.colorTextMuted());
            empty.setPadding(new Insets(30));
            checklistContainer.getChildren().add(empty);
            summaryLabel.setText("0 / 0 taken");
            return;
        }

        for (MedicineIntake intake : intakes) {
            checklistContainer.getChildren().add(buildIntakeRow(intake));
        }

        updateSummary(intakes);
    }

    private HBox buildIntakeRow(MedicineIntake intake) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color: " + UITheme.BG_SURFACE + "; "
                    + "-fx-background-radius: 10; "
                    + "-fx-border-color: " + UITheme.BORDER_COLOR + "; "
                    + "-fx-border-width: 1;");

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(intake.isTaken());
        checkBox.setStyle("-fx-cursor: hand;");

        Label dot = new Label("●");
        dot.setFont(Font.font(10));
        dot.setTextFill(intake.isTaken()
                ? UITheme.colorSuccess()
                : intake.isOverdue()
                    ? UITheme.colorError()
                    : Color.web("#f39c12"));

        VBox info = new VBox(2);
        Label medName = new Label("Medicine ID: " + intake.getMedicineId());
        medName.setFont(UITheme.fontHeading4());
        medName.setTextFill(UITheme.colorTextPrimary());

        String timeStr = intake.getScheduledTime() != null
                ? intake.getScheduledTime().format(TIME_FMT)
                : "--:--";
        Label timeLabel = new Label("Scheduled: " + timeStr);
        timeLabel.setFont(UITheme.fontSmall());
        timeLabel.setTextFill(UITheme.colorTextMuted());

        info.getChildren().addAll(medName, timeLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label badge = buildStatusBadge(intake);

        checkBox.setOnAction(e -> {
            boolean success =
                    notificationService.toggleIntakeStatus(intake.getIntakeId());
            if (success) {
                intake.toggleStatus();  
                dot.setTextFill(intake.isTaken()
                        ? UITheme.colorSuccess()
                        : UITheme.colorError());
                badge.setText(intake.isTaken() ? "✓ Taken" : "✗ Missed");
                badge.setStyle(intake.isTaken()
                        ? takenBadgeStyle() : missedBadgeStyle());
                refreshSummaryAfterToggle();
            } else {
                checkBox.setSelected(!checkBox.isSelected());
                showError("Failed to update intake status. Please try again.");
            }
        });

        row.getChildren().addAll(checkBox, dot, info, badge);
        return row;
    }

    private Label buildStatusBadge(MedicineIntake intake) {
        Label badge = new Label();
        if (intake.isTaken()) {
            badge.setText("✓ Taken");
            badge.setStyle(takenBadgeStyle());
        } else if (intake.isOverdue()) {
            badge.setText("✗ Missed");
            badge.setStyle(missedBadgeStyle());
        } else {
            badge.setText("⏳ Pending");
            badge.setStyle(pendingBadgeStyle());
        }
        return badge;
    }

    private String takenBadgeStyle() {
        return "-fx-background-color: #d4edda; -fx-text-fill: #155724; "
             + "-fx-padding: 4 10 4 10; -fx-background-radius: 12; "
             + "-fx-font-size: 12;";
    }

    private String missedBadgeStyle() {
        return "-fx-background-color: #f8d7da; -fx-text-fill: #721c24; "
             + "-fx-padding: 4 10 4 10; -fx-background-radius: 12; "
             + "-fx-font-size: 12;";
    }

    private String pendingBadgeStyle() {
        return "-fx-background-color: #fff3cd; -fx-text-fill: #856404; "
             + "-fx-padding: 4 10 4 10; -fx-background-radius: 12; "
             + "-fx-font-size: 12;";
    }

    private void updateSummary(List<MedicineIntake> intakes) {
        long taken = intakes.stream().filter(MedicineIntake::isTaken).count();
        summaryLabel.setText(taken + " / " + intakes.size() + " taken");
        summaryLabel.setTextFill(taken == intakes.size()
                ? UITheme.colorSuccess() : UITheme.colorTextMuted());
    }

    private void refreshSummaryAfterToggle() {
        try {
            List<MedicineIntake> intakes =
                    notificationService.getTodayIntakes(patientId);
            updateSummary(intakes);
        } catch (RuntimeException ex) {
            summaryLabel.setText("DB unavailable");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
