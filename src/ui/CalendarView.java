package ui;

import controller.NotificationService;
import model.MedicineIntake;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CalendarView extends VBox {

    private static final DateTimeFormatter MONTH_FMT =
            DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter TIME_FMT  =
            DateTimeFormatter.ofPattern("hh:mm a");

    private final NotificationService notificationService;
    private final String patientId;

    private YearMonth currentMonth;
    private GridPane calendarGrid;
    private Label monthLabel;
    private VBox dayDetailPanel;

    public CalendarView(String patientId) {
        this.patientId = patientId;
        this.notificationService = new NotificationService();
        this.currentMonth = YearMonth.now();

        setSpacing(0);
        buildUI();
        renderCalendar();
    }

    private void buildUI() {
        HBox navBar = new HBox(12);
        navBar.setAlignment(Pos.CENTER);
        navBar.setPadding(new Insets(16, 20, 12, 20));
        navBar.setStyle("-fx-background-color: " + UITheme.BG_SURFACE + "; "
                   + "-fx-border-color: " + UITheme.BORDER_COLOR + "; "
                       + "-fx-border-width: 0 0 1 0;");

        Button prevBtn = new Button("◀");
        prevBtn.setStyle(navBtnStyle());
        prevBtn.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            renderCalendar();
            clearDayDetail();
        });

        monthLabel = new Label();
        monthLabel.setFont(UITheme.fontHeading4());
        monthLabel.setTextFill(UITheme.colorTextPrimary());
        monthLabel.setMinWidth(200);
        monthLabel.setAlignment(Pos.CENTER);

        Button nextBtn = new Button("▶");
        nextBtn.setStyle(navBtnStyle());
        nextBtn.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            renderCalendar();
            clearDayDetail();
        });

        Button todayBtn = new Button("Today");
        todayBtn.setStyle(UITheme.getPrimaryButtonStyle() + " -fx-padding: 8 14 8 14; -fx-font-size: 12;");
        todayBtn.setOnAction(e -> {
            currentMonth = YearMonth.now();
            renderCalendar();
            showDayDetail(LocalDate.now());
        });

        navBar.getChildren().addAll(prevBtn, monthLabel, nextBtn,
                                    new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }},
                                    todayBtn);

        GridPane dayHeaders = new GridPane();
        dayHeaders.setPadding(new Insets(8, 20, 4, 20));
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < 7; i++) {
            Label lbl = new Label(days[i]);
            lbl.setFont(UITheme.fontSmall());
            lbl.setTextFill(UITheme.colorTextMuted());
            lbl.setPrefWidth(52);
            lbl.setAlignment(Pos.CENTER);
            dayHeaders.add(lbl, i, 0);
        }
        dayHeaders.setHgap(6);

        calendarGrid = new GridPane();
        calendarGrid.setPadding(new Insets(4, 20, 16, 20));
        calendarGrid.setHgap(6);
        calendarGrid.setVgap(6);

        dayDetailPanel = new VBox(10);
        dayDetailPanel.setPadding(new Insets(16));
        dayDetailPanel.setMinWidth(260);
        dayDetailPanel.setStyle("-fx-background-color: " + UITheme.BG_PRIMARY + "; "
                       + "-fx-border-color: " + UITheme.BORDER_COLOR + "; "
                               + "-fx-border-width: 0 0 0 1;");

        Label detailPlaceholder = new Label("Select a date to view\nscheduled medicines.");
        detailPlaceholder.setFont(UITheme.fontBody());
        detailPlaceholder.setTextFill(UITheme.colorTextMuted());
        detailPlaceholder.setAlignment(Pos.CENTER);
        dayDetailPanel.getChildren().add(detailPlaceholder);

        VBox calendarBody = new VBox(0, dayHeaders, calendarGrid);
        calendarBody.setStyle("-fx-background-color: " + UITheme.BG_SURFACE + ";");
        VBox.setVgrow(calendarBody, Priority.ALWAYS);

        HBox mainBody = new HBox(0, calendarBody, dayDetailPanel);
        HBox.setHgrow(calendarBody, Priority.ALWAYS);
        VBox.setVgrow(mainBody, Priority.ALWAYS);

        getChildren().addAll(navBar, mainBody);
    }

    private void renderCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentMonth.format(MONTH_FMT));

        LocalDate firstDay = currentMonth.atDay(1);
        int startCol = firstDay.getDayOfWeek().getValue() - 1; 
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        int col = startCol;
        int row = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            VBox cell      = buildDayCell(date, today);

            calendarGrid.add(cell, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    private VBox buildDayCell(LocalDate date, LocalDate today) {
        VBox cell = new VBox(2);
        cell.setPrefSize(52, 52);
        cell.setAlignment(Pos.TOP_CENTER);
        cell.setPadding(new Insets(4));

        boolean isToday   = date.equals(today);
        boolean isPast    = date.isBefore(today);

        String cellStyle;
        if (isToday) {
            cellStyle = "-fx-background-color: " + UITheme.TEAL_PRIMARY + "; "
                      + "-fx-background-radius: 8; -fx-cursor: hand;";
        } else if (isPast) {
            cellStyle = "-fx-background-color: #f0f0f0; "
                      + "-fx-background-radius: 8; -fx-cursor: hand;";
        } else {
            cellStyle = "-fx-background-color: " + UITheme.BG_SURFACE + "; "
                      + "-fx-background-radius: 8; -fx-cursor: hand; "
                      + "-fx-border-color: " + UITheme.BORDER_COLOR + "; -fx-border-radius: 8;";
        }
        cell.setStyle(cellStyle);

        Label dayLbl = new Label(String.valueOf(date.getDayOfMonth()));
        dayLbl.setFont(UITheme.fontLabel());
        dayLbl.setTextFill(isToday ? Color.WHITE
                 : isPast  ? UITheme.colorTextMuted()
                       : UITheme.colorTextPrimary());

        cell.getChildren().add(dayLbl);

        cell.setOnMouseEntered(e -> {
            if (!isToday) cell.setStyle(cellStyle
                    + " -fx-background-color: #e8f4fd; -fx-background-radius: 8;");
        });
        cell.setOnMouseExited(e -> cell.setStyle(cellStyle));

        cell.setOnMouseClicked(e -> showDayDetail(date));

        return cell;
    }

    private void showDayDetail(LocalDate date) {
        dayDetailPanel.getChildren().clear();

        DateTimeFormatter hdrFmt = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");
        Label dateHeader = new Label(date.format(hdrFmt));
        dateHeader.setFont(UITheme.fontHeading4());
        dateHeader.setTextFill(UITheme.colorTextPrimary());

        Separator sep = new Separator();

        dayDetailPanel.getChildren().addAll(dateHeader, sep);

        List<MedicineIntake> intakes;
        try {
            intakes = notificationService.getIntakesForDate(patientId, date);
        } catch (RuntimeException ex) {
            Label err = new Label("Database unavailable.\n" + ex.getMessage());
            err.setFont(UITheme.fontSmall());
            err.setTextFill(UITheme.colorError());
            err.setWrapText(true);
            dayDetailPanel.getChildren().add(err);
            return;
        }

        if (intakes.isEmpty()) {
            Label empty = new Label("No medicines scheduled\nfor this date.");
            empty.setFont(UITheme.fontBody());
            empty.setTextFill(UITheme.colorTextMuted());
            empty.setAlignment(Pos.CENTER);
            dayDetailPanel.getChildren().add(empty);
            return;
        }

        for (MedicineIntake intake : intakes) {
            dayDetailPanel.getChildren().add(buildDetailRow(intake));
        }
    }

    private HBox buildDetailRow(MedicineIntake intake) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: #ffffff; "
                    + "-fx-background-radius: 8;");

        Label dot = new Label("●");
        dot.setFont(Font.font(10));
        dot.setTextFill(intake.isTaken()
                ? Color.web("#27ae60")
                : intake.isOverdue()
                    ? Color.web("#e74c3c")
                    : Color.web("#f39c12"));

        VBox info = new VBox(2);
        Label medId = new Label("Med: " + intake.getMedicineId());
        medId.setFont(Font.font("System", FontWeight.BOLD, 13));
        medId.setTextFill(Color.web("#1a1a2e"));

        String time = intake.getScheduledTime() != null
                ? intake.getScheduledTime().format(TIME_FMT) : "--";
        Label timeLbl = new Label(time);
        timeLbl.setFont(Font.font("System", 12));
        timeLbl.setTextFill(Color.web("#888888"));

        info.getChildren().addAll(medId, timeLbl);

        Label badge = new Label(intake.isTaken() ? "✓"
                              : intake.isOverdue() ? "✗" : "⏳");
        badge.setFont(Font.font("System", FontWeight.BOLD, 14));
        badge.setTextFill(intake.isTaken()   ? Color.web("#27ae60")
                        : intake.isOverdue() ? Color.web("#e74c3c")
                                             : Color.web("#f39c12"));

        HBox.setHgrow(info, Priority.ALWAYS);
        row.getChildren().addAll(dot, info, badge);
        return row;
    }

    private void clearDayDetail() {
        dayDetailPanel.getChildren().clear();
        Label placeholder = new Label("Select a date to view\nscheduled medicines.");
        placeholder.setFont(Font.font("System", 13));
        placeholder.setTextFill(Color.web("#aaaaaa"));
        placeholder.setAlignment(Pos.CENTER);
        dayDetailPanel.getChildren().add(placeholder);
    }

    private String navBtnStyle() {
        return "-fx-background-color: #f0f0f0; -fx-background-radius: 6; "
             + "-fx-cursor: hand; -fx-font-size: 13;";
    }
}
