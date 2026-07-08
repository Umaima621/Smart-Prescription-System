package controller;

import controller.strategy.ReportPeriodStrategy;
import dao.IntakeLogDAO;
import dao.ReportDAO;
import model.MedicineIntake;
import model.Report;
import model.ReportStats;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReportGenerator {

    private final IntakeLogDAO intakeLogDAO;
    private final ReportDAO reportDAO;
    private ReportPeriodStrategy strategy;   

    public ReportGenerator() {
        this.intakeLogDAO = new IntakeLogDAO();
        this.reportDAO = new ReportDAO();
    }

    public void setStrategy(ReportPeriodStrategy strategy) {
        this.strategy = strategy;
    }

    public Report buildReport(String patientId, LocalDate startDate, LocalDate endDate) {

        if (strategy == null) {
            System.err.println("[ReportGenerator] No strategy set. Call setStrategy() first.");
            return null;
        }

        LocalDate resolvedStart = (startDate != null) ? startDate
                                  : strategy.getStartDate(LocalDate.now());
        LocalDate resolvedEnd   = (endDate   != null) ? endDate
                                  : strategy.getEndDate(LocalDate.now());

        List<MedicineIntake> intakeLogs = intakeLogDAO.queryIntakeLogs(patientId, resolvedStart, resolvedEnd);

        Report report = new Report(patientId, strategy.getPeriodType(), resolvedStart, resolvedEnd);
        report.setReportId(UUID.randomUUID().toString());
        report.setGeneratedAt(LocalDateTime.now());
        report.computeFromIntakeLogs(intakeLogs);  

        boolean saved = reportDAO.save(report);
        if (!saved) {
            System.err.println("[ReportGenerator] Failed to save report to DB.");
        }

        System.out.println("[ReportGenerator] Built: " + report);
        return report;
    }

    public ReportStats calculateStats(List<MedicineIntake> intakeLogs) {
        int scheduled = intakeLogs.size();
        int taken = (int) intakeLogs.stream() .filter(MedicineIntake::isTaken) .count();
        int missed = scheduled - taken;
        return new ReportStats(scheduled, taken, missed);
    }

    public String exportToPDF(Report report) {
        System.out.println("[ReportGenerator] exportToPDF() called for report: "
                           + report.getReportId());
        return null;
    }
}
