package controller;

import controller.strategy.MonthlyReportStrategy;
import controller.strategy.ReportPeriodStrategy;
import controller.strategy.WeeklyReportStrategy;
import dao.IntakeLogDAO;
import dao.ReportDAO;
import model.MedicineIntake;
import model.Report;

import java.io.File;
import java.time.LocalDate;
import java.util.List;


public class ReportController {

    private final ReportGenerator reportGenerator;
    private final IntakeLogDAO intakeLogDAO;
    private final ReportDAO reportDAO;

    public ReportController() {
        this.reportGenerator = new ReportGenerator();
        this.intakeLogDAO = new IntakeLogDAO();
        this.reportDAO = new ReportDAO();
    }

    public Report generateReport(String patientId, String type) {
        if (patientId == null || patientId.isBlank()) {
            System.err.println("[ReportController] generateReport: patientId is empty.");
            return null;
        }
        if (type == null || (!type.equals("weekly") && !type.equals("monthly"))) {
            System.err.println("[ReportController] generateReport: invalid type '" + type + "'.");
            return null;
        }

        ReportPeriodStrategy strategy = type.equals("weekly") ? new WeeklyReportStrategy() : new MonthlyReportStrategy();

        reportGenerator.setStrategy(strategy);

        return reportGenerator.buildReport(patientId, null, null);
    }

    public Report generateReportForPeriod(String patientId, String type, LocalDate startDate, LocalDate endDate) {
        if (patientId == null || patientId.isBlank()) return null;

        ReportPeriodStrategy strategy = (type != null && type.equals("monthly")) ? new MonthlyReportStrategy() : new WeeklyReportStrategy();

        reportGenerator.setStrategy(strategy);
        return reportGenerator.buildReport(patientId, startDate, endDate);
    }

    public List<MedicineIntake> viewMissedMedicines(String patientId, String period) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end;

        if ("monthly".equals(period)) {
            start = new MonthlyReportStrategy().getStartDate(today);
            end   = new MonthlyReportStrategy().getEndDate(today);
        } else {
            start = new WeeklyReportStrategy().getStartDate(today);
            end   = new WeeklyReportStrategy().getEndDate(today);
        }

        List<MedicineIntake> allLogs = intakeLogDAO.queryIntakeLogs(patientId, start, end);

        return allLogs.stream() .filter(intake -> !intake.isTaken() && intake.isOverdue()) .collect(java.util.stream.Collectors.toList());
    }

    public File exportReportPDF(String reportId) {
        Report report = reportDAO.findById(reportId);
        if (report == null) {
            System.err.println("[ReportController] exportReportPDF: report not found.");
            return null;
        }

        String pdfPath = reportGenerator.exportToPDF(report);
        return pdfPath != null ? new File(pdfPath) : null;
    }

    public Report getReportById(String reportId) {
        return reportDAO.findById(reportId);
    }

    public List<Report> getReportHistory(String patientId) {
        return reportDAO.findByPatient(patientId);
    }
}
