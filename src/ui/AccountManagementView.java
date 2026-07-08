package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AccountManagementView extends VBox {

    private TextField nameField;
    private TextField emailField;
    private PasswordField currentPassField;
    private PasswordField newPassField;
    private PasswordField confirmPassField;
    private Label roleLabel;
    private Label statusLabel;
    private String currentRole;
    private String currentUserId;

    public AccountManagementView(String role, String userId) {
        this.currentRole = role;
        this.currentUserId = userId;
        setSpacing(0);
        setPadding(new Insets(0));
        buildUI();
        loadUserData();
    }

    private void buildUI() {
        VBox header = new VBox(12);
        header.setPadding(new Insets(UITheme.PADDING_LARGE));
        header.setStyle(UITheme.getMainContainerStyle());
        Label title = new Label("Manage Your Account");
        title.setFont(UITheme.fontHeading3());
        title.setTextFill(UITheme.colorTextPrimary());
        Label subtitle = new Label("Update your profile information and password");
        subtitle.setFont(UITheme.fontSmall());
        subtitle.setTextFill(UITheme.colorTextMuted());
        header.getChildren().addAll(title, subtitle);
        VBox content = new VBox(UITheme.GAP_LARGE);
        content.setPadding(new Insets(UITheme.PADDING_LARGE));
        content.setStyle(UITheme.getMainContainerStyle());
        content.getChildren().add(buildProfileCard());
        content.getChildren().add(buildSecurityCard());
        statusLabel = new Label("");
        statusLabel.setFont(UITheme.fontSmall());
        statusLabel.setPadding(new Insets(UITheme.PADDING_SMALL));
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(UITheme.getMainContainerStyle());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getChildren().addAll(header, scrollPane, statusLabel);
    }

    private VBox buildProfileCard() {
        VBox card = new VBox(UITheme.GAP_MEDIUM);
        card.setPadding(new Insets(UITheme.PADDING_LARGE));
        card.setStyle(UITheme.getCardStyle());
        card.setMaxWidth(600);
        Label sectionTitle = new Label("Profile Information");
        sectionTitle.setFont(UITheme.fontHeading4());
        sectionTitle.setTextFill(UITheme.colorTextPrimary());
        GridPane grid = new GridPane();
        grid.setHgap(UITheme.GAP_MEDIUM);
        grid.setVgap(UITheme.GAP_MEDIUM);
        Label roleLbl = new Label("Account Type:");
        roleLbl.setFont(UITheme.fontLabel());
        roleLbl.setStyle(UITheme.getLabelStyle());
        roleLabel = new Label(currentRole.toUpperCase());
        roleLabel.setFont(UITheme.fontBody());
        roleLabel.setStyle("-fx-text-fill: " + UITheme.TEAL_PRIMARY + "; -fx-font-weight: bold;");
        grid.add(roleLbl, 0, 0);
        grid.add(roleLabel, 1, 0);
        Label nameLbl = new Label("Full Name:");
        nameLbl.setFont(UITheme.fontLabel());
        nameLbl.setStyle(UITheme.getLabelStyle());
        nameField = new TextField();
        nameField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        nameField.setStyle(UITheme.getTextFieldStyle());
        grid.add(nameLbl, 0, 1);
        grid.add(nameField, 1, 1);
        Label emailLbl = new Label("Email Address:");
        emailLbl.setFont(UITheme.fontLabel());
        emailLbl.setStyle(UITheme.getLabelStyle());
        emailField = new TextField();
        emailField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        emailField.setStyle(UITheme.getTextFieldStyle());
        grid.add(emailLbl, 0, 2);
        grid.add(emailField, 1, 2);
        HBox btnBox = new HBox(UITheme.GAP_MEDIUM);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle(UITheme.getPrimaryButtonStyle());
        saveBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        saveBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        saveBtn.setOnAction(e -> saveProfileChanges());
        btnBox.getChildren().add(saveBtn);
        card.getChildren().addAll(sectionTitle, new Separator(), grid, btnBox);
        return card;
    }

    private VBox buildSecurityCard() {
        VBox card = new VBox(UITheme.GAP_MEDIUM);
        card.setPadding(new Insets(UITheme.PADDING_LARGE));
        card.setStyle(UITheme.getCardStyle());
        card.setMaxWidth(600);
        Label sectionTitle = new Label("Change Password");
        sectionTitle.setFont(UITheme.fontHeading4());
        sectionTitle.setTextFill(UITheme.colorTextPrimary());
        GridPane grid = new GridPane();
        grid.setHgap(UITheme.GAP_MEDIUM);
        grid.setVgap(UITheme.GAP_MEDIUM);
        Label currentPassLbl = new Label("Current Password:");
        currentPassLbl.setFont(UITheme.fontLabel());
        currentPassLbl.setStyle(UITheme.getLabelStyle());
        currentPassField = new PasswordField();
        currentPassField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        currentPassField.setStyle(UITheme.getTextFieldStyle());
        grid.add(currentPassLbl, 0, 0);
        grid.add(currentPassField, 1, 0);
        Label newPassLbl = new Label("New Password:");
        newPassLbl.setFont(UITheme.fontLabel());
        newPassLbl.setStyle(UITheme.getLabelStyle());
        newPassField = new PasswordField();
        newPassField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        newPassField.setStyle(UITheme.getTextFieldStyle());
        grid.add(newPassLbl, 0, 1);
        grid.add(newPassField, 1, 1);
        Label confirmPassLbl = new Label("Confirm Password:");
        confirmPassLbl.setFont(UITheme.fontLabel());
        confirmPassLbl.setStyle(UITheme.getLabelStyle());
        confirmPassField = new PasswordField();
        confirmPassField.setMinHeight(UITheme.FIELD_MIN_HEIGHT);
        confirmPassField.setStyle(UITheme.getTextFieldStyle());
        grid.add(confirmPassLbl, 0, 2);
        grid.add(confirmPassField, 1, 2);
        HBox btnBox = new HBox(UITheme.GAP_MEDIUM);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button updatePassBtn = new Button("Update Password");
        updatePassBtn.setStyle(UITheme.getPrimaryButtonStyle());
        updatePassBtn.setMinWidth(UITheme.BUTTON_MIN_WIDTH);
        updatePassBtn.setMinHeight(UITheme.BUTTON_MIN_HEIGHT);
        updatePassBtn.setOnAction(e -> changePassword());
        btnBox.getChildren().add(updatePassBtn);
        card.getChildren().addAll(sectionTitle, new Separator(), grid, btnBox);
        return card;
    }

    private void loadUserData() {
        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE email = ? OR CAST(userID AS VARCHAR) = ?");
            stmt.setString(1, currentUserId);
            stmt.setString(2, currentUserId);
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
            } else {
                statusLabel.setText("Profile not found.");
                statusLabel.setStyle(UITheme.getErrorStyle());
            }
        } catch (java.sql.SQLException e) {
            statusLabel.setText("Error loading profile: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void saveProfileChanges() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty() || email.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }
        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE Users SET name = ?, email = ? WHERE email = ? OR CAST(userID AS VARCHAR) = ?");
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, currentUserId);
            stmt.setString(4, currentUserId);
            stmt.executeUpdate();
            statusLabel.setText("✓ Profile updated successfully");
            statusLabel.setStyle(UITheme.getSuccessStyle());
        } catch (java.sql.SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }

    private void changePassword() {
        String currentPass = currentPassField.getText();
        String newPass = newPassField.getText();
        String confirmPass = confirmPassField.getText();
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            statusLabel.setText("Please fill in all password fields");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }
        if (!newPass.equals(confirmPass)) {
            statusLabel.setText("New passwords do not match");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }
        if (newPass.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters");
            statusLabel.setStyle(UITheme.getErrorStyle());
            return;
        }
        try {
            java.sql.Connection conn = db.DatabaseConnection.getInstance().getConnection();
            java.sql.PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM Users WHERE (email = ? OR CAST(userID AS VARCHAR) = ?) AND password = ?");
            checkStmt.setString(1, currentUserId);
            checkStmt.setString(2, currentUserId);
            checkStmt.setString(3, currentPass);
            java.sql.ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                statusLabel.setText("Current password is incorrect");
                statusLabel.setStyle(UITheme.getErrorStyle());
                return;
            }
            java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE Users SET password = ? WHERE email = ? OR CAST(userID AS VARCHAR) = ?");
            stmt.setString(1, newPass);
            stmt.setString(2, currentUserId);
            stmt.setString(3, currentUserId);
            stmt.executeUpdate();
            statusLabel.setText("✓ Password changed successfully");
            statusLabel.setStyle(UITheme.getSuccessStyle());
            currentPassField.clear();
            newPassField.clear();
            confirmPassField.clear();
        } catch (java.sql.SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle(UITheme.getErrorStyle());
        }
    }
}