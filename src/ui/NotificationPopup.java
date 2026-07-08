package ui;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NotificationPopup {

    public enum Type { SUCCESS, ERROR, INFO }

    public static void show(Stage owner, String message, Type type) {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        HBox box = new HBox(12);
        box.setPadding(new Insets(14, 20, 14, 20));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMinWidth(320);
        box.setMaxWidth(420);

        String bgColor, icon;
        switch (type) {
            case SUCCESS:
                bgColor = "#2D7A6B";
                icon = "✓";
                break;
            case ERROR:
                bgColor = "#A02020";
                icon = "✗";
                break;
            default:
                bgColor = "#2C3E50";
                icon = "ℹ";
                break;
        }

        box.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 16, 0, 0, 4);"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setFont(javafx.scene.text.Font.font("Segoe UI",
                javafx.scene.text.FontWeight.BOLD, 18));
        iconLabel.setTextFill(Color.WHITE);

        Label msgLabel = new Label(message);
        msgLabel.setFont(javafx.scene.text.Font.font("Segoe UI", 13));
        msgLabel.setTextFill(Color.WHITE);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(360);

        box.getChildren().addAll(iconLabel, msgLabel);
        popup.getContent().add(box);

        double x = owner.getX() + owner.getWidth() / 2 - 180;
        double y = owner.getY() + 60;
        popup.show(owner, x, y);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), box);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), box);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> popup.hide());
            fadeOut.play();
        });
        pause.play();
    }
}