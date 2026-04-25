package model;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DoctorDashboard extends Application {

    static final String BG      = "#E8EEF4";
    static final String SURFACE = "#FFFFFF";
    static final String TEAL    = "#2D7A6B";
    static final String TEAL_H  = "#235F53";
    static final String TEXT    = "#2C3035";
    static final String MUTED   = "#7A8A96";
    static final String BORDER  = "#C8D4DC";
    static final String RED_ERR = "#A02020";

    private Stage primaryStage;
    private String selectedRole = "doctor";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showWelcome();
    }

    private void showWelcome() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + BG + ";");
        root.setPrefSize(700, 460);

        Arc arc = new Arc(0, 230, 160, 160, -60, 120);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.web(TEAL));
        arc.setStrokeWidth(3);
        arc.getStrokeDashArray().addAll(6.0, 8.0);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        StackPane.setAlignment(arc, Pos.CENTER_LEFT);

        VBox content = new VBox(28);
        content.setAlignment(Pos.CENTER);

        Label welcome = new Label("Welcome to\nMediCare");
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 46));
        welcome.setTextFill(Color.web(TEXT));
        welcome.setTextAlignment(TextAlignment.CENTER);

        Button getStarted = new Button("Get Started with MediCare");
        getStarted.setMinWidth(320);
        getStarted.setMinHeight(52);
        getStarted.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");
        getStarted.setOnAction(e -> crossFade(root, buildLoginScene()));

        content.getChildren().addAll(welcome, getStarted);
        root.getChildren().addAll(arc, content);

        primaryStage.setScene(new Scene(root, 700, 460));
        primaryStage.show();
    }

    private Scene buildLoginScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        VBox card = new VBox(0);
        card.setMaxWidth(400);
        card.setStyle("-fx-background-color: " + SURFACE + "; -fx-background-radius: 14; -fx-border-color: " + BORDER + "; -fx-border-width: 1; -fx-border-radius: 14;");

        Rectangle strip = new Rectangle(400, 5);
        strip.setFill(Color.web(TEAL));

        VBox inner = new VBox(0);
        inner.setPadding(new Insets(32, 36, 36, 36));

        Label heading = new Label("Sign in");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

        Label roleLabel = fieldLabel("Continue as");
        HBox roleRow    = buildRoleToggle();

        Label     nameLbl  = fieldLabel("Full Name");
        TextField nameFld  = buildField("Enter your full name");
        Label     emailLbl = fieldLabel("Email Address");
        TextField emailFld = buildField("name@example.com");
        Label     userLbl  = fieldLabel("Username or ID");
        TextField userFld  = buildField("DR-00142 or PT-00391");
        Label         passLbl = fieldLabel("Password");
        PasswordField passFld = buildPassField();

        Label errLabel = new Label();
        errLabel.setTextFill(Color.web(RED_ERR));
        errLabel.setVisible(false);

        Button loginBtn = new Button("Sign In");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setMinHeight(46);
        loginBtn.setStyle("-fx-background-color: " + TEAL + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand;");

        loginBtn.setOnAction(e -> {
            if (userFld.getText().isEmpty() || passFld.getText().isEmpty()) {
                errLabel.setText("Required fields missing.");
                errLabel.setVisible(true);
                shake(card);
            } else {
                Scene next = "doctor".equals(selectedRole)
                        ? buildPatientSearchScene()
                        : buildPatientScene();
                primaryStage.setScene(next);
            }
        });

        inner.getChildren().addAll(
            heading,   pad(20),
            roleLabel, roleRow,   pad(15),
            nameLbl,   nameFld,  pad(12),
            emailLbl,  emailFld, pad(12),
            userLbl,   userFld,  pad(12),
            passLbl,   passFld,  pad(10),
            errLabel,  pad(10),
            loginBtn
        );

        card.getChildren().addAll(strip, inner);

        ScrollPane sp = new ScrollPane(new StackPane(card));
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:transparent; -fx-background: " + BG + ";");

        return new Scene(sp, 700, 650);
    }

    private Scene buildPatientSearchScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");
        root.setTop(buildNavBar("Dr. Smith", "General Medicine"));

        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(230);
        sidebar.setPadding(new Insets(24, 14, 24, 14));
        sidebar.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: " + BORDER + "; -fx-border-width: 0 1 0 0;");

        Label navTitle = new Label("MENU");
        navTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        navTitle.setTextFill(Color.web(MUTED));
        navTitle.setPadding(new Insets(0, 0, 10, 8));
        sidebar.getChildren().add(navTitle);

//        addSidebarBtnActive(sidebar, "\uD83D\uDD0D", "Patient Records");
//        addSidebarBtn(sidebar, "\uD83D\uDC8A", "Prescribe Medicine", null);
//        addSidebarBtn(sidebar, "\u23F0", "Missed Medicines", null);
//        addSidebarBtn(sidebar, "\u2699", "Manage Account", null);

        addSidebarBtn(sidebar, "🔍", "Patient Records", () -> {
            root.setCenter(new ScrollPane(PatientSearchUI.getView()));
        });
        addSidebarBtn(sidebar, "💊", "Prescribe Medicine", () -> {
            root.setCenter(new ScrollPane(PrescribeMedicineUI.getView()));
        });

        addSidebarBtn(sidebar, "⏰", "Missed Medicines", () -> {
            root.setCenter(new ScrollPane(MissedMedicinesUI.getView()));
        });

        
        addSidebarBtn(sidebar, "\u2699", "Manage Account", null);

        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        addSidebarBtn(sidebar, "\u2192", "Logout", () -> primaryStage.setScene(buildLoginScene()));
        
     // Only add these ONCE
     // Inside buildPatientSearchScene() or your main controller
        
        
        VBox patientView = PatientSearchUI.getView();
        ScrollPane rightScroll = new ScrollPane(patientView);
        rightScroll.setFitToWidth(true);
        rightScroll.setFitToHeight(true);
        rightScroll.setStyle("-fx-background-color: transparent; -fx-background: " + BG + ";");

        root.setLeft(sidebar);
        root.setCenter(rightScroll);

        return new Scene(root, 1050, 700);
    }

    private Scene buildPatientScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");
        root.setTop(buildNavBar("Patient Portal", "Your health at a glance"));

        VBox content = new VBox(0);
        content.setPadding(new Insets(36, 52, 36, 52));

        Label title = new Label("Your Health\nDashboard");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        title.setPadding(new Insets(4, 0, 24, 0));

        VBox menu = new VBox(12);
        addDashCard(menu, "\uD83D\uDC8A", "My Prescriptions", "View current medications", null);
        addDashCard(menu, "\u2192", "Logout", "End your session", () -> primaryStage.setScene(buildLoginScene()));

        content.getChildren().addAll(title, menu);
        root.setCenter(new ScrollPane(content));

        return new Scene(root, 900, 600);
    }

    private HBox buildNavBar(String name, String role) {
        HBox bar = new HBox(15);
        bar.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");
        bar.setPadding(new Insets(14, 28, 14, 28));
        bar.setAlignment(Pos.CENTER_LEFT);

        Label brand = new Label("MediCare");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        brand.setTextFill(Color.web(TEAL));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        VBox user = new VBox(2, new Label(name), new Label(role));
        user.setAlignment(Pos.CENTER_RIGHT);

        bar.getChildren().addAll(brand, sp, user);
        return bar;
    }

    private void addSidebarBtn(VBox parent, String icon, String label, Runnable action) {
        HBox btn = new HBox(12);
        btn.setPadding(new Insets(11, 14, 11, 14));
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-background-radius: 8; -fx-cursor: hand;");
        btn.getChildren().addAll(new Label(icon), new Label(label));
        btn.setOnMouseClicked(e -> { if (action != null) action.run(); });
        parent.getChildren().add(btn);
    }

    private void addSidebarBtnActive(VBox parent, String icon, String label) {
        HBox btn = new HBox(10);
        btn.setPadding(new Insets(11, 14, 11, 10));
        btn.setStyle("-fx-background-color: #D6EDE8; -fx-background-radius: 8;");
        Label text = new Label(icon + "  " + label);
        text.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        text.setTextFill(Color.web(TEAL));
        btn.getChildren().add(text);
        parent.getChildren().add(btn);
    }

    private void addDashCard(VBox parent, String icon, String title, String subtitle, Runnable action) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("-fx-background-color: " + SURFACE + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-width: 1; -fx-cursor: hand;");
        VBox info = new VBox(2, new Label(title), new Label(subtitle));
        card.getChildren().addAll(new Label(icon), info);
        card.setOnMouseClicked(e -> { if (action != null) action.run(); });
        parent.getChildren().add(card);
    }

    private HBox buildRoleToggle() {
        Button d = new Button("Doctor");
        Button p = new Button("Patient");
        d.setPrefWidth(110); p.setPrefWidth(110);
        d.setStyle("-fx-background-color:" + TEAL + "; -fx-text-fill:white; -fx-background-radius: 6 0 0 6;");
        p.setStyle("-fx-background-color:" + BORDER + "; -fx-text-fill:" + TEXT + "; -fx-background-radius: 0 6 6 0;");
        d.setOnAction(e -> { selectedRole = "doctor"; d.setStyle("-fx-background-color:" + TEAL + "; -fx-text-fill:white;"); p.setStyle("-fx-background-color:" + BORDER + ";"); });
        p.setOnAction(e -> { selectedRole = "patient"; p.setStyle("-fx-background-color:" + TEAL + "; -fx-text-fill:white;"); d.setStyle("-fx-background-color:" + BORDER + ";"); });
        return new HBox(d, p);
    }

    private Label fieldLabel(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        l.setTextFill(Color.web(MUTED));
        return l;
    }

    private TextField buildField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMinHeight(40);
        tf.setStyle("-fx-background-color: " + BG + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");
        return tf;
    }

    private PasswordField buildPassField() {
        PasswordField pf = new PasswordField();
        pf.setPromptText("••••••••");
        pf.setMinHeight(40);
        pf.setStyle("-fx-background-color: " + BG + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6;");
        return pf;
    }

    private Region pad(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        return r;
    }

    private void shake(VBox node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setByX(10); tt.setCycleCount(4); tt.setAutoReverse(true); tt.play();
    }

    private void crossFade(StackPane root, Scene next) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), root);
        ft.setToValue(0);
        ft.setOnFinished(e -> primaryStage.setScene(next));
        ft.play();
    }

    public static void main(String[] args) { launch(args); }
}


