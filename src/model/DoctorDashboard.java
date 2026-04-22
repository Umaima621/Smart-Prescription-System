package model;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class DoctorDashboard extends Application {

    @Override
    public void start(Stage stage) {

        // Title
        Label title = new Label("Doctor Dashboard");
        title.setFont(new Font("Arial", 28));

        // Buttons
        Button btnPrescribe = new Button("Prescribe Medicine");
        Button btnViewRecord = new Button("View Patient Record");
        Button btnMissed = new Button("View Missed Medicines");
        Button btnAccount = new Button("Manage Account");
        Button btnLogout = new Button("Logout");

        // Style buttons
        for (Button b : new Button[]{btnPrescribe, btnViewRecord, btnMissed, btnAccount, btnLogout}) {
            b.setMinWidth(250);
            b.setStyle("-fx-font-size: 16px; -fx-padding: 10;");
        }

        // Layout
        VBox layout = new VBox(15);
        layout.getChildren().addAll(
                title,
                btnPrescribe,
                btnViewRecord,
                btnMissed,
                btnAccount,
                btnLogout
        );

        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 400, 500);

        stage.setTitle("Doctor Dashboard");
        stage.setScene(scene);
        stage.show();

        // Navigation placeholders
        btnPrescribe.setOnAction(e -> System.out.println("Open Prescribe Medicine Page"));
        btnViewRecord.setOnAction(e -> System.out.println("Open View Patient Record Page"));
        btnMissed.setOnAction(e -> System.out.println("Open Missed Medicines Page"));
        btnAccount.setOnAction(e -> System.out.println("Open Manage Account Page"));
        btnLogout.setOnAction(e -> System.out.println("Logging out..."));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
