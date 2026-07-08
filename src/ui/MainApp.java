package ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.ScrollPane;
import model.UserFactory;

public class MainApp extends Application {
	 
    private Stage primaryStage;
    private BorderPane root;
    private String currentRole;
    private String currentUserId;
    private Button activeNavBtn;
    public static Stage mainStage;
    private ReminderNotificationService reminderService;
 
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainStage = primaryStage;
        primaryStage.setTitle("MediCare - Smart Prescription System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
 
        showWelcomeScreen();
        primaryStage.show();
    }
 
    private void showWelcomeScreen() {
        StackPane welcomePane = new StackPane();
        welcomePane.setStyle(UITheme.getMainContainerStyle());
 
        Arc arc = new Arc(0, 230, 160, 160, -60, 120);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(UITheme.colorTealPrimary());
        arc.setStrokeWidth(3);
        arc.getStrokeDashArray().addAll(6.0, 8.0);
        arc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        StackPane.setAlignment(arc, Pos.CENTER_LEFT);
 
        VBox content = new VBox(28);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(500);
 
        Label welcome = new Label("Welcome to\nMediCare");
        welcome.setFont(UITheme.fontHeading1());
        welcome.setTextFill(UITheme.colorTextPrimary());
        welcome.setTextAlignment(TextAlignment.CENTER);
 
        Button getStarted = new Button("Get Started with MediCare");
        getStarted.setMinWidth(320);
        getStarted.setMinHeight(52);
        getStarted.setStyle(UITheme.getPrimaryButtonStyle());
        getStarted.setOnAction(e -> showLoginScreen());
 
        content.getChildren().addAll(welcome, getStarted);
        welcomePane.getChildren().addAll(arc, content);
 
        primaryStage.setScene(new Scene(welcomePane));
    }
 
    private void showLoginScreen() {
        StackPane loginPane = new StackPane();
        loginPane.setStyle(UITheme.getMainContainerStyle());
 
        VBox card = new VBox(0);
        card.setMaxWidth(450);
        card.setStyle(UITheme.getCardStyle() + " -fx-padding: 0;");
 
        Region strip = new Region();
        strip.setPrefSize(450, 8);
        strip.setStyle("-fx-background-color: " + UITheme.TEAL_PRIMARY + ";");
 
        VBox inner = new VBox(20);
        inner.setPadding(new Insets(UITheme.PADDING_LARGE));
 
        Label heading = new Label("Sign in");
        heading.setFont(UITheme.fontHeading2());
        heading.setTextFill(UITheme.colorTextPrimary());
 
        Label roleLabel = buildFieldLabel("Continue as");
        HBox roleRow = buildRoleToggle();
 
        Label userLbl = buildFieldLabel("Email");
        TextField userFld = buildInputField("Enter your email address");
 
        Label passLbl = buildFieldLabel("Password");
        PasswordField passFld = buildPasswordField();
 
        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefHeight(48);
        loginBtn.setStyle(UITheme.getPrimaryButtonStyle());
        loginBtn.setOnAction(e -> handleLogin(userFld.getText(), passFld.getText()));
        
        Button registerLink = new Button("Don't have an account? Register");
        registerLink.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UITheme.TEAL_PRIMARY + "; -fx-cursor: hand; -fx-border-color: transparent;");
        registerLink.setMaxWidth(Double.MAX_VALUE);
        registerLink.setOnAction(e -> showRegisterScreen());
 
        Button forgotPasswordLink = new Button("Forgot Password?");
        forgotPasswordLink.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UITheme.TEAL_PRIMARY + "; -fx-cursor: hand; -fx-border-color: transparent;");
        forgotPasswordLink.setMaxWidth(Double.MAX_VALUE);
        forgotPasswordLink.setOnAction(e -> showForgotPasswordScreen());
 
        inner.getChildren().addAll(
                heading,
                new Separator(),
                roleLabel, roleRow,
                userLbl, userFld,
                passLbl, passFld,
                loginBtn,
                forgotPasswordLink,
                registerLink
        );
 
        card.getChildren().addAll(strip, inner);
        loginPane.getChildren().add(card);
 
        primaryStage.setScene(new Scene(loginPane));
    }
    
    private void showRegisterScreen() {
        StackPane registerPane = new StackPane();
        registerPane.setStyle(UITheme.getMainContainerStyle());
 
        VBox card = new VBox(0);
        card.setMaxWidth(450);
        card.setStyle(UITheme.getCardStyle() + " -fx-padding: 0;");
 
        Region strip = new Region();
        strip.setPrefSize(450, 8);
        strip.setStyle("-fx-background-color: " + UITheme.TEAL_PRIMARY + ";");
 
        VBox inner = new VBox(15);
        inner.setPadding(new Insets(UITheme.PADDING_LARGE));
 
        Label heading = new Label("Create Account");
        heading.setFont(UITheme.fontHeading2());
        heading.setTextFill(UITheme.colorTextPrimary());
 
        Label roleLabel = buildFieldLabel("Register as");
        HBox roleRow = buildRoleToggle();
 
        Label nameLbl = buildFieldLabel("Full Name");
        TextField nameFld = buildInputField("Enter your full name");
 
        Label emailLbl = buildFieldLabel("Email Address");
        TextField emailFld = buildInputField("name@example.com");
 
        Label passLbl = buildFieldLabel("Password");
        PasswordField passFld = buildPasswordField();
 
        Label confirmLbl = buildFieldLabel("Confirm Password");
        PasswordField confirmFld = buildPasswordField();
 
        Label specLbl = buildFieldLabel("Specialization");
        TextField specFld = buildInputField("e.g. General Medicine");
        Label licenseLbl = buildFieldLabel("License Number");
        TextField licenseFld = buildInputField("e.g. DR-00142");
 
        VBox doctorFields = new VBox(10);
        doctorFields.getChildren().addAll(specLbl, specFld, licenseLbl, licenseFld);
 
        Label bloodLbl = buildFieldLabel("Blood Type");
        TextField bloodFld = buildInputField("e.g. O+");
 
        VBox patientFields = new VBox(10);
        patientFields.getChildren().addAll(bloodLbl, bloodFld);
 
        if ("doctor".equals(currentRole)) {
            doctorFields.setVisible(true);
            doctorFields.setManaged(true);
            patientFields.setVisible(false);
            patientFields.setManaged(false);
        } else {
            doctorFields.setVisible(false);
            doctorFields.setManaged(false);
            patientFields.setVisible(true);
            patientFields.setManaged(true);
        }
 
        RadioButton dBtn = (RadioButton) roleRow.getChildren().get(0);
        RadioButton pBtn = (RadioButton) roleRow.getChildren().get(1);
 
        dBtn.setOnAction(e -> {
            currentRole = "doctor";
            doctorFields.setVisible(true);
            doctorFields.setManaged(true);
            patientFields.setVisible(false);
            patientFields.setManaged(false);
        });
 
        pBtn.setOnAction(e -> {
            currentRole = "patient";
            doctorFields.setVisible(false);
            doctorFields.setManaged(false);
            patientFields.setVisible(true);
            patientFields.setManaged(true);
        });
 
        Label statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontSmall());
        statusLabel.setWrapText(true);
 
        Button registerBtn = new Button("Create Account");
        registerBtn.setPrefHeight(48);
        registerBtn.setStyle(UITheme.getPrimaryButtonStyle());
        registerBtn.setOnAction(e -> {
            String name     = nameFld.getText().trim();
            String email    = emailFld.getText().trim();
            String password = passFld.getText().trim();
            String confirm  = confirmFld.getText().trim();
 
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all required fields.");
                statusLabel.setStyle(UITheme.getErrorStyle());
                return;
            }
            if (!password.equals(confirm)) {
                statusLabel.setText("Passwords do not match.");
                statusLabel.setStyle(UITheme.getErrorStyle());
                return;
            }
            if (password.length() < 6) {
                statusLabel.setText("Password must be at least 6 characters.");
                statusLabel.setStyle(UITheme.getErrorStyle());
                return;
            }
 
            try {
                java.sql.Connection conn =
                        db.DatabaseConnection.getInstance().getConnection();
 
                String checkSql = "SELECT COUNT(*) FROM Users WHERE email = ?";
                java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, email);
                java.sql.ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    statusLabel.setText("Email already registered. Please login.");
                    statusLabel.setStyle(UITheme.getErrorStyle());
                    return;
                }
 
                String role = "doctor".equals(currentRole) ? "Doctor" : "Patient";
                String sql = "INSERT INTO Users (name, email, password, role, " +
                            "specialization, licenseNumber, bloodType) VALUES (?,?,?,?,?,?,?)";
                java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, role);
 
                if ("doctor".equals(currentRole)) {
                    stmt.setString(5, specFld.getText().trim());
                    stmt.setString(6, licenseFld.getText().trim());
                    stmt.setNull(7, java.sql.Types.VARCHAR);
                } else {
                    stmt.setNull(5, java.sql.Types.VARCHAR);
                    stmt.setNull(6, java.sql.Types.VARCHAR);
                    stmt.setString(7, bloodFld.getText().trim());
                }
                stmt.executeUpdate();
 
                statusLabel.setText("✓ Account created! You can now sign in.");
                statusLabel.setStyle(UITheme.getSuccessStyle());
                NotificationPopup.show(mainStage, "Account created successfully!", NotificationPopup.Type.SUCCESS);
 
                nameFld.clear(); emailFld.clear();
                passFld.clear(); confirmFld.clear();
                specFld.clear(); licenseFld.clear();
                bloodFld.clear();
 
            } catch (java.sql.SQLException ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                statusLabel.setStyle(UITheme.getErrorStyle());
            }
        });
 
        Button backBtn = new Button("Already have an account? Sign In");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: "
                + UITheme.TEAL_PRIMARY + "; -fx-cursor: hand; -fx-border-color: transparent;");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> showLoginScreen());
 
        inner.getChildren().addAll(
                heading, new Separator(),
                roleLabel, roleRow,
                nameLbl, nameFld,
                emailLbl, emailFld,
                doctorFields, patientFields,
                passLbl, passFld,
                confirmLbl, confirmFld,
                statusLabel,
                registerBtn, backBtn
        );
 
        card.getChildren().addAll(strip, inner);
 
        ScrollPane sp = new ScrollPane(new StackPane(card));
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: "
                + UITheme.BG_PRIMARY + ";");
 
        registerPane.getChildren().add(sp);
        primaryStage.setScene(new Scene(registerPane, 1200, 800));
    }
 
    private void showForgotPasswordScreen() {
        StackPane forgotPane = new StackPane();
        forgotPane.setStyle(UITheme.getMainContainerStyle());
 
        VBox card = new VBox(0);
        card.setMaxWidth(450);
        card.setStyle(UITheme.getCardStyle() + " -fx-padding: 0;");
 
        Region strip = new Region();
        strip.setPrefSize(450, 8);
        strip.setStyle("-fx-background-color: " + UITheme.TEAL_PRIMARY + ";");
 
        VBox inner = new VBox(20);
        inner.setPadding(new Insets(UITheme.PADDING_LARGE));
 
        Label heading = new Label("Reset Password");
        heading.setFont(UITheme.fontHeading2());
        heading.setTextFill(UITheme.colorTextPrimary());
 
        Label subtitle = new Label("Enter your account details and choose a new password.");
        subtitle.setFont(UITheme.fontSmall());
        subtitle.setTextFill(UITheme.colorTextMuted());
        subtitle.setWrapText(true);
 
        Label roleLabel = buildFieldLabel("Account Type");
        HBox roleRow = buildRoleToggle();
 
        Label emailLbl = buildFieldLabel("Email");
        TextField emailFld = buildInputField("Enter your registered email");
 
        Label newPassLbl = buildFieldLabel("New Password");
        PasswordField newPassFld = buildPasswordField();
 
        Label confirmPassLbl = buildFieldLabel("Confirm New Password");
        PasswordField confirmPassFld = buildPasswordField();
 
        Label statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontSmall());
        statusLabel.setWrapText(true);
 
        Button resetBtn = new Button("Reset Password");
        resetBtn.setPrefHeight(48);
        resetBtn.setStyle(UITheme.getPrimaryButtonStyle());
        resetBtn.setOnAction(e -> {
            String email = emailFld.getText().trim();
            String role = "doctor".equals(currentRole) ? "Doctor" : "Patient";
            String newPassword = newPassFld.getText().trim();
            String confirmPassword = confirmPassFld.getText().trim();
 
            if (email.isEmpty() || newPassword.isEmpty()) {
                statusLabel.setText("Please fill in all required fields.");
                statusLabel.setStyle(UITheme.getErrorStyle());
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                statusLabel.setText("Passwords do not match.");
                statusLabel.setStyle(UITheme.getErrorStyle());
                return;
            }
            if (newPassword.length() < 6) {
                statusLabel.setText("Password must be at least 6 characters.");
                statusLabel.setStyle(UITheme.getErrorStyle());
                return;
            }
 
            try {
                java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
 
                String checkSql = "SELECT COUNT(*) FROM Users WHERE email = ? AND role = ?";
                java.sql.PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, email);
                checkStmt.setString(2, role);
                java.sql.ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    statusLabel.setText("No " + role.toLowerCase() + " account found with that email.");
                    statusLabel.setStyle(UITheme.getErrorStyle());
                    return;
                }
 
                String updateSql = "UPDATE Users SET password = ? WHERE email = ? AND role = ?";
                java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, email);
                updateStmt.setString(3, role);
                updateStmt.executeUpdate();
 
                statusLabel.setText("✓ Password reset! You can now sign in.");
                statusLabel.setStyle(UITheme.getSuccessStyle());
                NotificationPopup.show(mainStage, "Password reset successfully!", NotificationPopup.Type.SUCCESS);
 
                emailFld.clear();
                newPassFld.clear();
                confirmPassFld.clear();
 
            } catch (java.sql.SQLException ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                statusLabel.setStyle(UITheme.getErrorStyle());
            }
        });
 
        Button backBtn = new Button("Back to Sign In");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: "
                + UITheme.TEAL_PRIMARY + "; -fx-cursor: hand; -fx-border-color: transparent;");
        backBtn.setMaxWidth(Double.MAX_VALUE);
        backBtn.setOnAction(e -> showLoginScreen());
 
        inner.getChildren().addAll(
                heading, subtitle,
                new Separator(),
                roleLabel, roleRow,
                emailLbl, emailFld,
                newPassLbl, newPassFld,
                confirmPassLbl, confirmPassFld,
                statusLabel,
                resetBtn, backBtn
        );
 
        card.getChildren().addAll(strip, inner);
 
        ScrollPane sp = new ScrollPane(new StackPane(card));
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: "
                + UITheme.BG_PRIMARY + ";");
 
        forgotPane.getChildren().add(sp);
        primaryStage.setScene(new Scene(forgotPane, 1200, 800));
    }
 
    private HBox buildRoleToggle() {
        HBox box = new HBox(12);
        box.setAlignment(Pos.CENTER_LEFT);
 
        ToggleGroup group = new ToggleGroup();
 
        RadioButton doctorRadio = new RadioButton("Doctor");
        doctorRadio.setToggleGroup(group);
        doctorRadio.setSelected(true);
        doctorRadio.setFont(UITheme.fontBody());
        doctorRadio.setOnAction(e -> currentRole = "doctor");
 
        RadioButton patientRadio = new RadioButton("Patient");
        patientRadio.setToggleGroup(group);
        patientRadio.setFont(UITheme.fontBody());
        patientRadio.setOnAction(e -> currentRole = "patient");
 
        currentRole = "doctor";
        box.getChildren().addAll(doctorRadio, patientRadio);
        return box;
    }
 
    private Label buildFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(UITheme.fontLabel());
        lbl.setStyle(UITheme.getLabelStyle());
        return lbl;
    }
 
    private TextField buildInputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMinHeight(44);
        tf.setStyle(UITheme.getTextFieldStyle());
        return tf;
    }
 
    private PasswordField buildPasswordField() {
        PasswordField pf = new PasswordField();
        pf.setPromptText("Enter your password");
        pf.setMinHeight(44);
        pf.setStyle(UITheme.getTextFieldStyle());
        return pf;
    }
 
    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Please fill in all fields");
            return;
        }
 
        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
            String role = "doctor".equals(currentRole) ? "Doctor" : "Patient";
 
            String sql = "SELECT * FROM Users WHERE email = ? AND password = ? AND role = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            java.sql.ResultSet rs = stmt.executeQuery();
 
            if (rs.next()) {
                currentUserId = rs.getString("email");
                String userName = rs.getString("name");
                String userRole = rs.getString("role");
 
                Object userObj = UserFactory.createUser(
                    userRole,
                    rs.getInt("userID"),
                    userName,
                    currentUserId,
                    userRole.equals("Doctor") ?
                        rs.getString("specialization") :
                        rs.getString("bloodType")
                );
                System.out.println("[MainApp] User created via Factory: " + userObj);
 
                showMainDashboard();
            } else {
                showAlert("Invalid email, password or role. Please check your credentials.");
            }
 
        } catch (java.sql.SQLException e) {
            showAlert("Database error: " + e.getMessage());
        }
    }
 
    private void showMainDashboard() {
        root = new BorderPane();
        if ("patient".equals(currentRole)) {
            reminderService = new ReminderNotificationService(currentUserId);
            reminderService.start();
        }
        root.setStyle(UITheme.getMainContainerStyle());
        root.setLeft(buildSidebar());
        showScreen("home");
 
        Scene mainScene = new Scene(root, 1200, 800);
        primaryStage.setScene(mainScene);
    }
 
    private VBox buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: " + UITheme.TEAL_DARK + ";");
 
        VBox brand = new VBox(8);
        brand.setPadding(new Insets(UITheme.PADDING_LARGE));
        brand.setStyle("-fx-border-color: " + UITheme.TEAL_PRIMARY + "; -fx-border-width: 0 0 2 0;");
 
        Label appName = new Label("MediCare");
        appName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        appName.setTextFill(Color.WHITE);
 
        Label roleLabel = new Label(currentRole.toUpperCase() + " PORTAL");
        roleLabel.setFont(UITheme.fontSmall());
        roleLabel.setTextFill(UITheme.colorTealLight());
 
        brand.getChildren().addAll(appName, roleLabel);
 
        VBox navButtons = new VBox(4);
        navButtons.setPadding(new Insets(UITheme.PADDING_MEDIUM, 0, 0, 0));
 
        if ("patient".equals(currentRole)) {
            navButtons.getChildren().addAll(
                    buildNavButton("💊 Add Prescription", "prescription"),
                    buildNavButton("🏥 Manage Medicines", "medicines"),
                    buildNavButton("⏰ Schedule Reminder", "reminder"),
                    buildNavButton("☑ Daily Checklist", "checklist"),
                    buildNavButton("📅 Calendar Schedule", "calendar"),
                    buildNavButton("✓ Confirm Intake", "confirm"),
                    buildNavButton("📊 Reports", "report"),
                    new Separator()
            );
        } else {
        	navButtons.getChildren().addAll(
        	        buildNavButton("👥 Missed Medicines", "missed"),
        	        buildNavButton("🔬 Prescribe Medicine", "prescribe"),
        	        buildNavButton("📤 Send Prescription", "send"),
        	        buildNavButton("📋 Patient Records", "records"),
        	        new Separator()
        	);
        }
 
        navButtons.getChildren().addAll(
                buildNavButton("⚙ Account Settings", "account"),
                buildNavButton("ℹ Dashboard", "home")
        );
 
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
 
        VBox footer = new VBox(8);
        footer.setPadding(new Insets(UITheme.PADDING_MEDIUM));
        footer.setStyle("-fx-border-color: " + UITheme.TEAL_PRIMARY + "; -fx-border-width: 2 0 0 0;");
 
        Label userInfo = new Label("Logged in as");
        userInfo.setFont(UITheme.fontSmall());
        userInfo.setTextFill(UITheme.colorTealLight());
 
        Label userId = new Label(currentUserId);
        userId.setFont(UITheme.fontLabel());
        userId.setTextFill(Color.WHITE);
 
        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(200);
        logoutBtn.setStyle("-fx-background-color: " + UITheme.COLOR_ERROR + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            if (reminderService != null) {
                reminderService.stop();
                reminderService = null;
            }
            showLoginScreen();
        });
 
        footer.getChildren().addAll(userInfo, userId, new Separator(), logoutBtn);
        sidebar.getChildren().addAll(brand, navButtons, spacer, footer);
        return sidebar;
    }
 
    private Button buildNavButton(String text, String screenId) {
        Button btn = new Button(text);
        btn.setPrefWidth(260);
        btn.setPrefHeight(48);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, UITheme.PADDING_MEDIUM, 0, UITheme.PADDING_LARGE));
        btn.setFont(UITheme.fontBody());
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UITheme.TEAL_LIGHT + "; -fx-cursor: hand;");
 
        btn.setOnAction(e -> {
            if (activeNavBtn != null) {
                activeNavBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UITheme.TEAL_LIGHT + "; -fx-cursor: hand;");
            }
            btn.setStyle("-fx-background-color: " + UITheme.TEAL_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            activeNavBtn = btn;
            showScreen(screenId);
        });
 
        btn.setOnMouseEntered(e -> {
            if (btn != activeNavBtn) {
                btn.setStyle("-fx-background-color: " + UITheme.TEAL_PRIMARY + "; -fx-text-fill: white; -fx-cursor: hand;");
            }
        });
 
        btn.setOnMouseExited(e -> {
            if (btn != activeNavBtn) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + UITheme.TEAL_LIGHT + "; -fx-cursor: hand;");
            }
        });
 
        return btn;
    }
 
    private void showScreen(String screenId) {
        try {
            if ("patient".equals(currentRole)) {
                switch (screenId) {
                	case "prescription":
                		root.setCenter(new AddPrescriptionView(currentUserId));
                		break;
                	case "medicines":
                		root.setCenter(new ManageMedicineView(currentUserId));
                		break;
                	case "reminder":
                		root.setCenter(new ScheduleReminderView(currentUserId));
                		break;
                    case "checklist":
                        root.setCenter(new DailyChecklistView(currentUserId));
                        break;
                    case "calendar":
                        root.setCenter(new CalendarView(currentUserId));
                        break;
                    case "confirm":
                        root.setCenter(buildPlaceholder("Confirm Intake", "This screen opens as a popup from the Daily Checklist when you click a medicine."));
                        break;
                    case "report":
                        root.setCenter(new ReportView(currentUserId));
                        break;
                    case "account":
                        root.setCenter(new AccountManagementView(currentRole, currentUserId));
                        break;
                    case "home":
                    default:
                        root.setCenter(buildPatientDashboard());
                        break;
                }
            } else {
                switch (screenId) {
                    case "missed":
                        root.setCenter(new MissedMedicinesView());
                        break;
                    case "prescribe":
                        root.setCenter(new PrescribeMedicineView(currentUserId));
                        break;
                    case "send":
                        root.setCenter(new SendPrescriptionView(currentUserId));
                        break;
                    case "records":
                        root.setCenter(new PatientRecordView());
                        break;
                    case "account":
                        root.setCenter(new AccountManagementView(currentRole, currentUserId));
                        break;
                    case "home":
                    default:
                        root.setCenter(buildDoctorDashboard());
                        break;
                }
            }
        } catch (RuntimeException ex) {
            root.setCenter(buildErrorView(ex.getMessage()));
        }
    }
 
    private VBox buildPatientDashboard() {
        VBox dashboard = new VBox(UITheme.GAP_LARGE);
        dashboard.setPadding(new Insets(UITheme.PADDING_LARGE));
        dashboard.setStyle(UITheme.getMainContainerStyle());
 
        Label title = new Label("Patient Dashboard");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());
 
        HBox statsBox = new HBox(UITheme.GAP_LARGE);
        statsBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
 
        int totalMedicines = 0, takenToday = 0, pending = 0;
        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
            String sql = "SELECT COUNT(*) FROM Medicines WHERE userID = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentUserId);
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) totalMedicines = rs.getInt(1);
 
            String sql2 = "SELECT COUNT(*) FROM medicine_intake WHERE patient_id = ? AND is_taken = 1 AND CAST(scheduled_time AS DATE) = CAST(GETDATE() AS DATE)";
            java.sql.PreparedStatement stmt2 = conn.prepareStatement(sql2);
            stmt2.setString(1, currentUserId);
            java.sql.ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) takenToday = rs2.getInt(1);
 
            pending = totalMedicines - takenToday;
            if (pending < 0) pending = 0;
 
        } catch (java.sql.SQLException e) {
            System.out.println("Stats error: " + e.getMessage());
        }
 
        statsBox.getChildren().addAll(
                buildStatCard("My Medicines", String.valueOf(totalMedicines), UITheme.TEAL_PRIMARY),
                buildStatCard("Taken Today", String.valueOf(takenToday), UITheme.COLOR_SUCCESS),
                buildStatCard("Pending", String.valueOf(pending), UITheme.COLOR_WARNING)
        );
 
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.setFont(UITheme.fontHeading4());
        quickActionsLabel.setTextFill(UITheme.colorTextPrimary());
 
        HBox actionsBox = new HBox(UITheme.GAP_MEDIUM);
        Button viewChecklistBtn = new Button("View Daily Checklist");
        viewChecklistBtn.setStyle(UITheme.getPrimaryButtonStyle());
        viewChecklistBtn.setOnAction(e -> showScreen("checklist"));
 
        Button viewReportsBtn = new Button("View Reports");
        viewReportsBtn.setStyle(UITheme.getPrimaryButtonStyle());
        viewReportsBtn.setOnAction(e -> showScreen("report"));
 
        actionsBox.getChildren().addAll(viewChecklistBtn, viewReportsBtn);
        dashboard.getChildren().addAll(title, statsBox, quickActionsLabel, actionsBox);
        return dashboard;
    }
 
    private VBox buildDoctorDashboard() {
        VBox dashboard = new VBox(UITheme.GAP_LARGE);
        dashboard.setPadding(new Insets(UITheme.PADDING_LARGE));
        dashboard.setStyle(UITheme.getMainContainerStyle());
 
        Label title = new Label("Doctor Dashboard");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());
 
        HBox statsBox = new HBox(UITheme.GAP_LARGE);
        statsBox.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10;");
 
        int totalPatients = 0, prescriptionsToday = 0, missedDoses = 0;
        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
 
            String sql1 = "SELECT COUNT(*) FROM Users WHERE role = 'Patient'";
            java.sql.PreparedStatement stmt1 = conn.prepareStatement(sql1);
            java.sql.ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) totalPatients = rs1.getInt(1);
 
            String sql2 = "SELECT COUNT(*) FROM Prescriptions WHERE doctorID = ? AND CAST(prescribedDate AS DATE) = CAST(GETDATE() AS DATE)";
            java.sql.PreparedStatement stmt2 = conn.prepareStatement(sql2);
            stmt2.setString(1, currentUserId);
            java.sql.ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) prescriptionsToday = rs2.getInt(1);
 
            String sql3 = "SELECT COUNT(*) FROM medicine_intake WHERE is_taken = 0 AND scheduled_time < GETDATE()";
            java.sql.PreparedStatement stmt3 = conn.prepareStatement(sql3);
            java.sql.ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) missedDoses = rs3.getInt(1);
 
        } catch (java.sql.SQLException e) {
            System.out.println("Doctor stats error: " + e.getMessage());
        }
 
        statsBox.getChildren().addAll(
                buildStatCard("Active Patients", String.valueOf(totalPatients), UITheme.TEAL_PRIMARY),
                buildStatCard("Prescriptions Today", String.valueOf(prescriptionsToday), UITheme.COLOR_SUCCESS),
                buildStatCard("Missed Doses", String.valueOf(missedDoses), UITheme.COLOR_WARNING)
        );
 
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.setFont(UITheme.fontHeading4());
        quickActionsLabel.setTextFill(UITheme.colorTextPrimary());
 
        HBox actionsBox = new HBox(UITheme.GAP_MEDIUM);
        Button prescribeBtn = new Button("Create Prescription");
        prescribeBtn.setStyle(UITheme.getPrimaryButtonStyle());
        prescribeBtn.setOnAction(e -> showScreen("prescribe"));
 
        Button viewMissedBtn = new Button("View Missed Doses");
        viewMissedBtn.setStyle(UITheme.getPrimaryButtonStyle());
        viewMissedBtn.setOnAction(e -> showScreen("missed"));
 
        actionsBox.getChildren().addAll(prescribeBtn, viewMissedBtn);
        dashboard.getChildren().addAll(title, statsBox, quickActionsLabel, actionsBox);
        return dashboard;
    }
 
    private VBox buildStatCard(String label, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: " + color + "; -fx-border-width: 3 0 0 0;");
        card.setPrefWidth(200);
 
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        valueLabel.setTextFill(Color.web(color));
 
        Label labelText = new Label(label);
        labelText.setFont(UITheme.fontSmall());
        labelText.setTextFill(UITheme.colorTextMuted());
 
        card.getChildren().addAll(valueLabel, labelText);
        return card;
    }
 
    private VBox buildPlaceholder(String title, String message) {
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(UITheme.PADDING_LARGE));
        box.setStyle(UITheme.getMainContainerStyle());
 
        Label titleLabel = new Label(title);
        titleLabel.setFont(UITheme.fontHeading3());
        titleLabel.setTextFill(UITheme.colorTextPrimary());
 
        Label messageLabel = new Label(message);
        messageLabel.setFont(UITheme.fontBody());
        messageLabel.setTextFill(UITheme.colorTextMuted());
        messageLabel.setWrapText(true);
 
        box.getChildren().addAll(titleLabel, messageLabel);
        return box;
    }
 
    private VBox buildErrorView(String errorMsg) {
        VBox box = new VBox(16);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        box.setStyle(UITheme.getMainContainerStyle());
 
        Label title = new Label("Error Loading Screen");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorError());
 
        Label error = new Label("Details: " + errorMsg);
        error.setFont(UITheme.fontSmall());
        error.setTextFill(UITheme.colorError());
        error.setWrapText(true);
 
        box.getChildren().addAll(title, error);
        return box;
    }
 
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}