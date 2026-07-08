package model;

import java.time.LocalDateTime;

public class MedicineIntake {

    private String intakeId;
    private String notificationId;
    private String medicineId;
    private String patientId;
    private LocalDateTime scheduledTime;
    private LocalDateTime takenAt;
    private boolean isTaken;

    public MedicineIntake(String intakeId, String notificationId,
                          String medicineId, String patientId,
                          LocalDateTime scheduledTime, LocalDateTime takenAt,
                          boolean isTaken) {
        this.intakeId = intakeId;
        this.notificationId = notificationId;
        this.medicineId = medicineId;
        this.patientId = patientId;
        this.scheduledTime = scheduledTime;
        this.takenAt = takenAt;
        this.isTaken = isTaken;
    }

    public MedicineIntake(String medicineId, String patientId,
                          LocalDateTime scheduledTime) {
        this.medicineId = medicineId;
        this.patientId = patientId;
        this.scheduledTime = scheduledTime;
        this.isTaken = false;
    }

    public void markAsTaken(LocalDateTime timestamp) {
        this.isTaken = true;
        this.takenAt = timestamp;
    }

    public void markAsMissed() {
        this.isTaken = false;
        this.takenAt = null;
    }

    public void toggleStatus() {
        if (this.isTaken) {
            markAsMissed();
        } else {
            markAsTaken(LocalDateTime.now());
        }
    }

    public boolean isOverdue() {
        return !isTaken && scheduledTime != null
                && LocalDateTime.now().isAfter(scheduledTime);
    }

    public String getIntakeId() { return intakeId; }
    public void setIntakeId(String intakeId) { this.intakeId = intakeId; }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getMedicineId() { return medicineId; }
    public void setMedicineId(String medicineId) { this.medicineId = medicineId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getTakenAt() { return takenAt; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }

    public boolean isTaken() { return isTaken; }
    public void setTaken(boolean taken) { isTaken = taken; }

    @Override
    public String toString() {
        return "MedicineIntake{" +
                "intakeId='"      + intakeId      + '\'' +
                ", medicineId='"  + medicineId    + '\'' +
                ", patientId='"   + patientId     + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", takenAt="      + takenAt       +
                ", isTaken="      + isTaken       +
                '}';
    }
}
