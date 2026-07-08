package ui;

import controller.NotificationService;
import model.MedicineIntake;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.format.DateTimeFormatter;

public class ConfirmIntakeView extends VBox {

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a");

    private final NotificationService notificationService;
    private final MedicineIntake intake;

    private Runnable onConfirmedCallback;

    public ConfirmIntakeView(MedicineIntake intake) {
        this.intake = intake;
        this.notificationService = new NotificationService();

        setSpacing(16);
        setPadding(new Insets(28));
        setAlignment(Pos.CENTER);
        setStyle(UITheme.getCardStyle() + " -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 16, 0, 0, 4);");
        setMaxWidth(420);

        buildUI();
    }

    private void buildUI() {
        Label icon = new Label("🔔");
        icon.setFont(UITheme.fontHeading1());
        icon.setAlignment(Pos.CENTER);

        Label title = new Label("Medicine Reminder");
        title.setFont(UITheme.fontHeading4());
        title.setTextFill(UITheme.colorTextPrimary());

        VBox infoCard = buildInfoCard();

        String scheduledStr = intake.getScheduledTime() != null
                ? intake.getScheduledTime().format(DT_FMT)
                : "N/A";
        Label timeLabel = new Label("Scheduled:  " + scheduledStr);
        timeLabel.setFont(UITheme.fontBody());
        timeLabel.setTextFill(UITheme.colorTextMuted());

        HBox buttons = buildButtons();

        Label statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontLabel());
        statusLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(icon, title, infoCard, timeLabel, buttons, statusLabel);
    }

    private VBox buildInfoCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #f6faf9; "
                + "-fx-background-radius: 10; -fx-border-color: " + UITheme.BORDER_COLOR + "; -fx-border-radius: 10;");

        Label medLabel = new Label("Medicine");
        medLabel.setFont(UITheme.fontSmall());
        medLabel.setTextFill(UITheme.colorTextMuted());

        Label medName = new Label("ID: " + intake.getMedicineId());
        medName.setFont(UITheme.fontHeading4());
        medName.setTextFill(UITheme.colorTextPrimary());

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + UITheme.BORDER_COLOR + ";");

        Label statusRow = new Label(intake.isTaken()
                ? "✓  Already marked as taken"
                : "⏳  Not yet confirmed");
        statusRow.setFont(UITheme.fontBody());
        statusRow.setTextFill(intake.isTaken()
            ? UITheme.colorSuccess() : Color.web("#e67e22"));

        card.getChildren().addAll(medLabel, medName, sep, statusRow);
        return card;
    }

    private HBox buildButtons() {
        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER);

        Button confirmBtn = new Button("✓  Mark as Taken");
        confirmBtn.setPrefWidth(180);
        confirmBtn.setPrefHeight(42);
        confirmBtn.setStyle(UITheme.getPrimaryButtonStyle());

        Button dismissBtn = new Button("Dismiss");
        dismissBtn.setPrefWidth(120);
        dismissBtn.setPrefHeight(42);
        dismissBtn.setStyle(UITheme.getSecondaryButtonStyle());

        confirmBtn.setOnAction(e -> {
            if (intake.isTaken()) {
                showInfo("This medicine is already marked as taken.");
                return;
            }

            boolean success =
                    notificationService.confirmIntake(intake.getIntakeId());

            if (success) {
                intake.markAsTaken(java.time.LocalDateTime.now());

                showSuccessFeedback(confirmBtn, dismissBtn);

                if (onConfirmedCallback != null) {
                    onConfirmedCallback.run();
                }
            } else {
                showError("Failed to confirm intake. Please try again.");
            }
        });

        dismissBtn.setOnAction(e -> {
            notificationService.markAsDismissed(intake.getNotificationId());
            closeView();
        });

        box.getChildren().addAll(confirmBtn, dismissBtn);
        return box;
    }

    private void showSuccessFeedback(Button confirmBtn, Button dismissBtn) {
        confirmBtn.setDisable(true);
        dismissBtn.setDisable(true);

        VBox successBox = new VBox(8);
        successBox.setAlignment(Pos.CENTER);

        Label checkmark = new Label("✓");
        checkmark.setFont(UITheme.fontHeading1());
        checkmark.setTextFill(UITheme.colorSuccess());

        Label msg = new Label("Intake confirmed!");
        msg.setFont(UITheme.fontHeading4());
        msg.setTextFill(UITheme.colorSuccess());

        Label subMsg = new Label("Great job taking your medicine on time.");
        subMsg.setFont(UITheme.fontBody());
        subMsg.setTextFill(UITheme.colorTextMuted());

        successBox.getChildren().addAll(checkmark, msg, subMsg);

        int buttonIndex = getChildren().indexOf(confirmBtn.getParent());
        if (buttonIndex >= 0) {
            getChildren().set(buttonIndex, successBox);
        } else {
            getChildren().add(successBox);
        }
    }

    public void setOnConfirmedCallback(Runnable callback) {
        this.onConfirmedCallback = callback;
    }

    public static void showAsDialog(MedicineIntake intake, Runnable onConfirmed) {
        ConfirmIntakeView view = new ConfirmIntakeView(intake);
        view.setOnConfirmedCallback(onConfirmed);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Medicine Reminder");
        dialog.getDialogPane().setContent(view);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setStyle("-fx-background-color: transparent; "
                                       + "-fx-padding: 0;");
        dialog.showAndWait();
    }

    private void closeView() {
        getScene().getWindow().hide();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
