package model;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Report {

    private String reportId;
    private String patientId;
    private String reportType;      
    private LocalDateTime generatedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalScheduled;
    private int totalTaken;
    private int totalMissed;

    private List<MedicineIntake> missedMedicines;

    public Report(String reportId, String patientId, String reportType,
                  LocalDateTime generatedAt, LocalDate startDate, LocalDate endDate,
                  int totalScheduled, int totalTaken, int totalMissed) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.reportType = reportType;
        this.generatedAt = generatedAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalScheduled = totalScheduled;
        this.totalTaken = totalTaken;
        this.totalMissed = totalMissed;
        this.missedMedicines = new ArrayList<>();
    }

    /** Minimal constructor — used when generating a new report */
    public Report(String patientId, String reportType,
                  LocalDate startDate, LocalDate endDate) {
        this.patientId = patientId;
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedAt = LocalDateTime.now();
        this.missedMedicines = new ArrayList<>();
    }

    public List<MedicineIntake> getMissedMedicines() {
        return new ArrayList<>(missedMedicines);
    }

    public float calculateComplianceRate() {
        if (totalScheduled == 0) return 0.0f;
        return ((float) totalTaken / totalScheduled) * 100.0f;
    }

    public File exportAsPDF() {
        return null;
    }

    public void computeFromIntakeLogs(List<MedicineIntake> intakeLogs) {
        this.totalScheduled = intakeLogs.size();
        this.totalTaken     = 0;
        this.missedMedicines.clear();

        for (MedicineIntake intake : intakeLogs) {
            if (intake.isTaken()) {
                this.totalTaken++;
            } else {
                this.missedMedicines.add(intake);
            }
        }
        this.totalMissed = this.totalScheduled - this.totalTaken;
    }
    
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getTotalScheduled() { return totalScheduled; }
    public void setTotalScheduled(int totalScheduled) {
        this.totalScheduled = totalScheduled;
    }

    public int getTotalTaken() { return totalTaken; }
    public void setTotalTaken(int totalTaken) { this.totalTaken = totalTaken; }

    public int getTotalMissed() { return totalMissed; }
    public void setTotalMissed(int totalMissed) { this.totalMissed = totalMissed; }

    public void setMissedMedicines(List<MedicineIntake> missedMedicines) {
        this.missedMedicines = missedMedicines;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId='"      + reportId      + '\'' +
                ", patientId='"   + patientId     + '\'' +
                ", reportType='"  + reportType    + '\'' +
                ", period="       + startDate     + " to " + endDate +
                ", scheduled="    + totalScheduled +
                ", taken="        + totalTaken    +
                ", missed="       + totalMissed   +
                ", compliance="   + String.format("%.1f", calculateComplianceRate()) + "%" +
                '}';
    }
}
