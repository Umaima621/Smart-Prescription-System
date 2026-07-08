package ui;

import controller.ReportController;
import model.MedicineIntake;
import model.Report;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.List;


public class ReportView extends VBox {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final ReportController reportController;
    private final String           patientId;

    private ToggleGroup  periodGroup;
    private VBox         statsPanel;
    private VBox         missedListPanel;
    private Button       exportBtn;
    private Report       currentReport;

    public ReportView(String patientId) {
        this.patientId        = patientId;
        this.reportController = new ReportController();

        setSpacing(0);
        buildUI();
    }

    private void buildUI() {
        HBox header = new HBox();
        header.setPadding(new Insets(16, 20, 12, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + UITheme.BG_SURFACE + "; "
                   + "-fx-border-color: " + UITheme.BORDER_COLOR + "; "
                       + "-fx-border-width: 0 0 1 0;");

        Label title = new Label("Medicine Report");
        title.setFont(UITheme.fontHeading4());
        title.setTextFill(UITheme.colorTextPrimary());
        header.getChildren().add(title);

        HBox controls = buildControlsBar();

        statsPanel = new VBox(12);
        statsPanel.setPadding(new Insets(20));
        statsPanel.setVisible(false);
        statsPanel.setManaged(false);

        missedListPanel = new VBox(8);
        missedListPanel.setPadding(new Insets(0, 20, 20, 20));
        missedListPanel.setVisible(false);
        missedListPanel.setManaged(false);

        ScrollPane scrollPane = new ScrollPane(
                new VBox(0, statsPanel, missedListPanel));
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(UITheme.getMainContainerStyle());
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(header, controls, scrollPane);
    }

    private HBox buildControlsBar() {
        HBox bar = new HBox(16);
        bar.setPadding(new Insets(16, 20, 16, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + UITheme.BG_PRIMARY + ";");

        Label periodLbl = new Label("Period:");
        periodLbl.setFont(UITheme.fontLabel());
        periodLbl.setTextFill(UITheme.colorTextPrimary());

        periodGroup = new ToggleGroup();

        RadioButton weeklyRb = new RadioButton("Weekly");
        weeklyRb.setToggleGroup(periodGroup);
        weeklyRb.setSelected(true);
        weeklyRb.setUserData("weekly");
        weeklyRb.setFont(UITheme.fontBody());

        RadioButton monthlyRb = new RadioButton("Monthly");
        monthlyRb.setToggleGroup(periodGroup);
        monthlyRb.setUserData("monthly");
        monthlyRb.setFont(UITheme.fontBody());

        Button generateBtn = new Button("Generate Report");
        generateBtn.setStyle(UITheme.getPrimaryButtonStyle());
        generateBtn.setOnAction(e -> generateReport());

        exportBtn = new Button();
        exportBtn.setVisible(false);
        exportBtn.setManaged(false);

        bar.getChildren().addAll(periodLbl, weeklyRb, monthlyRb,
                new Separator(javafx.geometry.Orientation.VERTICAL),
                generateBtn);
        return bar;
    }

    private void generateReport() {
        String period = (String) periodGroup.getSelectedToggle().getUserData();
        try {
            controller.MedicareFacade facade = controller.MedicareFacade.getInstance();
            currentReport = period.equals("weekly")
                    ? facade.generateWeeklyReport(patientId)
                    : facade.generateMonthlyReport(patientId);
        } catch (RuntimeException ex) {
            showError("Database unavailable.\n" + ex.getMessage());
            return;
        }

        if (currentReport == null) {
            showError("Failed to generate report. Please try again.");
            return;
        }

        showStats(currentReport);
        showMissedList(period);

       
    }

    private void showStats(Report report) {
        statsPanel.getChildren().clear();

        String periodStr = report.getStartDate().format(DATE_FMT)
                         + "  →  "
                         + report.getEndDate().format(DATE_FMT);
        Label periodLabel = new Label(report.getReportType().toUpperCase()
                                     + " REPORT  |  " + periodStr);
        periodLabel.setFont(UITheme.fontSmall());
        periodLabel.setTextFill(UITheme.colorTextMuted());

        HBox cards = new HBox(12);
        cards.setAlignment(Pos.CENTER_LEFT);
        cards.getChildren().addAll(
            buildStatCard("Scheduled", report.getTotalScheduled(), UITheme.TEAL_PRIMARY),
            buildStatCard("Taken",     report.getTotalTaken(),     UITheme.COLOR_SUCCESS),
            buildStatCard("Missed",    report.getTotalMissed(),    UITheme.COLOR_ERROR)
        );

        float rate = report.calculateComplianceRate();
        VBox rateBox = buildComplianceBar(rate);

        statsPanel.getChildren().addAll(periodLabel, cards, rateBox);
        statsPanel.setVisible(true);
        statsPanel.setManaged(true);
    }

    private VBox buildStatCard(String label, int value, String color) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(16, 24, 16, 24));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: " + UITheme.BG_SURFACE + "; "
                + "-fx-background-radius: 10; "
                + "-fx-border-color: " + color + "; -fx-border-width: 2 0 0 0;");
        card.setPrefWidth(120);

        Label numLabel = new Label(String.valueOf(value));
        numLabel.setFont(UITheme.fontHeading1());
        numLabel.setTextFill(Color.web(color));

        Label txtLabel = new Label(label);
        txtLabel.setFont(UITheme.fontSmall());
        txtLabel.setTextFill(UITheme.colorTextMuted());

        card.getChildren().addAll(numLabel, txtLabel);
        return card;
    }

    private VBox buildComplianceBar(float rate) {
        VBox box = new VBox(6);

        HBox labelRow = new HBox();
        Label compLabel = new Label("Compliance Rate");
        compLabel.setFont(UITheme.fontLabel());
        compLabel.setTextFill(UITheme.colorTextPrimary());
        Label rateLabel = new Label(String.format("%.1f%%", rate));
        rateLabel.setFont(UITheme.fontLabel());
        rateLabel.setTextFill(rate >= 80 ? UITheme.colorSuccess()
                    : rate >= 50 ? Color.web(UITheme.COLOR_WARNING)
                         : UITheme.colorError());
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        labelRow.getChildren().addAll(compLabel, sp, rateLabel);

        ProgressBar bar = new ProgressBar(rate / 100.0);
        bar.setPrefWidth(Double.MAX_VALUE);
        bar.setPrefHeight(12);
        String barColor = rate >= 80 ? UITheme.COLOR_SUCCESS
                : rate >= 50 ? UITheme.COLOR_WARNING : UITheme.COLOR_ERROR;
        bar.setStyle("-fx-accent: " + barColor + ";");

        box.getChildren().addAll(labelRow, bar);
        return box;
    }

    private void showMissedList(String period) {
        missedListPanel.getChildren().clear();

        List<MedicineIntake> missed;
        try {
        	missed = controller.MedicareFacade.getInstance().getMissedMedicines(patientId, period);
        } catch (RuntimeException ex) {
            showError("Unable to load missed medicines.\n" + ex.getMessage());
            return;
        }

        Label sectionTitle = new Label("Missed Medicines  (" + missed.size() + ")");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        sectionTitle.setTextFill(Color.web("#1a1a2e"));
        sectionTitle.setPadding(new Insets(8, 0, 4, 0));

        missedListPanel.getChildren().add(sectionTitle);

        if (missed.isEmpty()) {
            Label noneLabel = new Label("✓  No missed medicines in this period!");
            noneLabel.setFont(Font.font("System", 13));
            noneLabel.setTextFill(Color.web("#27ae60"));
            noneLabel.setPadding(new Insets(12, 0, 0, 0));
            missedListPanel.getChildren().add(noneLabel);
        } else {
            for (MedicineIntake intake : missed) {
                missedListPanel.getChildren().add(buildMissedRow(intake));
            }
        }

        missedListPanel.setVisible(true);
        missedListPanel.setManaged(true);
    }

    private HBox buildMissedRow(MedicineIntake intake) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color: #fff5f5; "
                    + "-fx-background-radius: 8;");

        Label icon = new Label("✗");
        icon.setFont(Font.font("System", FontWeight.BOLD, 14));
        icon.setTextFill(Color.web("#e74c3c"));

        VBox info = new VBox(2);
        Label medId = new Label("Medicine ID: " + intake.getMedicineId());
        medId.setFont(Font.font("System", FontWeight.BOLD, 13));
        medId.setTextFill(Color.web("#1a1a2e"));

        String scheduled = intake.getScheduledTime() != null
                ? intake.getScheduledTime().format(
                    DateTimeFormatter.ofPattern("dd MMM  hh:mm a"))
                : "Unknown time";
        Label timeLbl = new Label("Was scheduled: " + scheduled);
        timeLbl.setFont(Font.font("System", 12));
        timeLbl.setTextFill(Color.web("#888888"));

        info.getChildren().addAll(medId, timeLbl);
        row.getChildren().addAll(icon, info);
        return row;
    }

    private void exportPDF() {
        if (currentReport == null) return;

        java.io.File pdf = reportController.exportReportPDF(
                currentReport.getReportId());

        if (pdf != null) {
            showInfo("PDF exported to:\n" + pdf.getAbsolutePath());
        } else {
            showInfo("PDF export coming soon.\nReport ID: "
                     + currentReport.getReportId());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
